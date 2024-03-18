# Database migration

In this task you will migrate the Drupal database to the new RDS database instance.

![Schema](./img/CLD_AWS_INFA.PNG)

<!-- Open the SSH tunnel to the Drupal machine : -->
<!-- ssh devopsteam18@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem -NvL 1337:10.0.18.10:22 & -->
<!-- ssh bitnami@localhost -p 1337 -i .ssh/CLD_KEY_DRUPAL_DEVOPSTEAM18.pem -->

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

[INPUT]

```bash
// Help : path /home/bitnami/bitnami_credentials
cat /home/bitnami/bitnami_credentials
```

[OUTPUT]

<!-- TODO show the output -->

```bash

```

### Get Database Name of Drupal

[INPUT]

```bash
mariadb -u username -p'password' -e "SHOW databases;" # Use the password from the previous step
```

[OUTPUT]

<!-- TODO show the output -->

```bash

```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

[INPUT]

```bash
mariadb-dump -u <db_user> -p"password" --databases <drupal_db_name> > dumpfile.sql
```

<!-- TODO show the output -->

[OUTPUT]

```bash

```

### Create the new Data base on RDS

[INPUT]

```sql
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p \
    -e "CREATE DATABASE bitnami_drupal;" -- Check DB name
```

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h" parameter.

[OUTPUT]

<!-- TODO show the output -->

```bash

```

[INPUT]

```bash
# SSH to application-a host then run:
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p <db_target> < dumpfile.sql
```

[OUTPUT]

```bash

```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

[INPUT]

```bash
//help : same settings.php as before
cat sites/default/settings.php
```


[OUTPUT]

<!-- TODO show the output -->

```php
//at the end of the file you will find connection string parameters
//username = bn_drupal
//password = XXXXXXX
```

### Replace the current host with the RDS FQDN

[INPUT]

```bash
sed -i "s/^'host' => .*/  'host' => 'dbi-devopsteam99.cshki92s4w5p.eu-west-3.rds.amazonaws.com',/" sites/default/settings.php
```

[OUTPUT]

<!-- TODO show the output -->

```bash
//settings.php

$databases['default']['default'] = array (
   [...] 
  'host' => 'dbi-devopsteam99.cshki92s4w5p.eu-west-3.rds.amazonaws.com',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.
* [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
* [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
* [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

[INPUT]

```bash
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p \
    -e "CREATE USER bn_drupal@'10.0.18.0/28'; GRANT ALL PRIVILEGES ON bitnami_drupal.* TO bn_drupal;"

mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p \
    -e "CREATE USER bn_drupal@'10.0.18.128/28'; GRANT ALL PRIVILEGES ON bitnami_drupal.* TO bn_drupal;"

# DO NOT FORGET TO FLUSH PRIVILEGES -> Not needed when using "GRANT"
```

[OUTPUT]

```bash
# TODO
```

[INPUT]

```sql
// Validation
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p \
    -e "SHOW GRANTS for bn_drupal@'10.0.18.0/28';"

mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u <rds_admin_user> -p \
    -e "SHOW GRANTS for bn_drupal@'10.0.128.0/28"
```

[OUTPUT]

<!-- TODO show the grants -->

```txt
+--------------------------------------------------------------------------------------------------------+
| Grants for <yourNewUser>                                                                               |
+--------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO <yourNewUser> IDENTIFIED BY PASSWORD 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO <yourNewUser>                                            |
+--------------------------------------------------------------------------------------------------------+
```

### Validate access (on the drupal instance)

```sql
[INPUT]
mysql -h dbi-devopsteam18.xxxxxxxx.eu-west-3.rds.amazonaws.com -u bn_drupal -p

[INPUT]
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
2 rows in set (0.001 sec)
```

* Repeat the procedure to enable the instance on subnet 2 to also talk to your RDS instance.

### Extra

* Cleanup

