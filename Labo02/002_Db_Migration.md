# Database migration

In this task you will migrate the Drupal database to the new RDS database
instance.

![Schema](./img/CLD_AWS_INFA.PNG)

<!-- Open the SSH tunnel to the Drupal machine : -->
<!-- ssh devopsteam18@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem -NvL 1337:10.0.18.10:22 -->
<!-- ssh bitnami@localhost -p 1337 -i .ssh/CLD_KEY_DRUPAL_DEVOPSTEAM18.pem -->

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

\[INPUT\]

```bash
// Help : path /home/bitnami/bitnami_credentials
cat /home/bitnami/bitnami_credentials
```

\[OUTPUT\]

```txt
Welcome to the Bitnami package for Drupal

******************************************************************************
The default username and password is 'user' and '+o@QCmvbVAZ2'.
******************************************************************************

You can also use this password to access the databases and any other component the stack includes.

Please refer to https://docs.bitnami.com/ for more details.
```

**Note**: default MariaDB account is called 'root' and not 'user'. The default username returned in the precedent step is Bitnami's user, not MariaDB's one. Dumping the users from MariaDB shows:

```txt
MariaDB [(none)]> SELECT User FROM mysql.user;
+-------------+
| User        |
+-------------+
| bn_drupal   |
| root        |
| mariadb.sys |
+-------------+
3 rows in set (0.008 sec)
```

### Get Database Name of Drupal

\[INPUT\]

```bash
mariadb -u root -p'+o@QCmvbVAZ2' -e "SHOW databases;" # Use the password from the previous step
```

\[OUTPUT\]

```txt
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| test               |
+--------------------+
```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

\[INPUT\]

```bash
mariadb-dump -u root -p'+o@QCmvbVAZ2' --databases bitnami_drupal > dumpfile.sql
```

\[OUTPUT\]

The command silently succeeds. `wc -l` on the file indeed shows that it contains
about 4000 lines.

### Create the new Data base on RDS

\[INPUT\]

```sql
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u admin -p'DEVOPSTEAM18!' \
    -e "CREATE DATABASE bitnami_drupal;"
```

\[OUTPUT\]

The command silently succeeds. Running `SHOW DATABASES` displays the newly
created 'bitnami_drupal' database.

```txt
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| drupal             |
| information_schema |
| innodb             |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h"
parameter.

\[INPUT\]

```bash
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
        -u admin -p'DEVOPSTEAM18!' < dumpfile.sql
```

\[OUTPUT\]

The command silently succeeds. Using `SHOW TABLES` displays the newly imported
tables.

```txt
+----------------------------------+
| Tables_in_bitnami_drupal         |
+----------------------------------+
| block_content                    |
| block_content__body              |

[...]

| users_field_data                 |
| watchdog                         |
+----------------------------------+
```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

\[INPUT\]

```bash
cat stack/drupal/sites/default/settings.php
```

\[OUTPUT\]

```php
$databases['default']['default'] = array (
  'database' => 'bitnami_drupal',
  'username' => 'bn_drupal',
  'password' => 'a138798bc8d2f05ad6622993f87dad5684be936c93e9490df6bb5523bda52eba',
  [...]
);
```

### Replace the current host with the RDS FQDN

\[INPUT\]

```bash
sudo sed -i "/\s\s'host' => '127.0.0.1',/c\ \ 'host' => 'dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com'," \
    stack/drupal/sites/default/settings.php
```

\[OUTPUT\]

The command silently succeeds. Cat'ing the file shows the value was corretcly
changed.

```bash
//settings.php

$databases['default']['default'] = array (
   [...] 
  'host' => 'dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.

- [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
- [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
- [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

\[INPUT\]

```bash
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u admin -p'DEVOPSTEAM18!' \
    -e "GRANT ALL PRIVILEGES ON bitnami_drupal.* TO 'bn_drupal'@'10.0.18.%' IDENTIFIED BY 'a138798bc8d2f05ad6622993f87dad5684be936c93e9490df6bb5523bda52eba';"

# DO NOT FORGET TO FLUSH PRIVILEGES -> Not needed when using "GRANT" on modern
#                                      MariaDB versions
```

\[INPUT\]

```bash
// Validation
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u admin -p'DEVOPSTEAM18!' \
    -e "SHOW GRANTS for bn_drupal@'10.0.18.%';"
```

\[OUTPUT\]

```txt
+------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.18.%                                                                                   |
+------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.18.%` IDENTIFIED BY PASSWORD '*66C49CE3DDF87D93C5987E78A9111E4667BA465E' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.18.%`                                            |
+------------------------------------------------------------------------------------------------------------------+
```

### Validate access (on the drupal instance)

\[INPUT\]

```bash
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u bn_drupal -pa138798bc8d2f05ad6622993f87dad5684be936c93e9490df6bb5523bda52eba \
    bitnami_drupal -e "SHOW DATABASES;"
```

\[OUTPUT\]

```txt
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
```

- Repeat the procedure to enable the instance on subnet 2 to also talk to your
  RDS instance.

There is no need to repeat the procedure because the `bn_drupal` user was
created with permissions on the `10.0.18.%` subnet, which effectively includes
only our two `/28` subnets using the wildcard character.

### Extra

Cleanup:

- Removed the `stack/drupal/sites/default/settings.php.bak` file
- Removed the `dumpfile.sql` file

<!-- Anything else to cleanup ? -->

- TODO `sudo systemctl disable mariadb`