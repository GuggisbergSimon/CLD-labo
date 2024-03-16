# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

|Key|Value for GUI Only|
|:--|:--|
|Name|AMI_DRUPAL_DEVOPSTEAM18_LABO02_RDS|
|Description|Same as name value|

```bash
[INPUT]


aws ec2 create-image \
    --instance-id <instance id of drupal server A> \
    --name "AMI_PRIVATE_DRUPAL_DEVOPSTEAM18_LABO02_RDS" \
    --description "AMI_PRIVATE_DRUPAL_DEVOPSTEAM18_LABO02_RDS"
[OUTPUT]

```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1

* Deploy Drupal Instance based on AMI in Az2

|Key|Value for GUI Only|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_B|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 run-instances \
                   --image-id <new image id from last step> \
                   --count 1 \
                   --instance-type t3.micro \
                   --key-name CLD_KEY_DRUPAL_DEVOPSTEAM18 \
                   --private-ip-address 10.0.18.10 \
                   --security-group-ids sg-060333a9f2656e446 sg-059f0b49f5ca4aab8 \
                   --subnet-id subnet-0bd3b8cdf25b8042e \
                   --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_A}]'

aws ec2 run-instances \
                   --image-id <new image id from last step> \
                   --count 1 \
                   --instance-type t3.micro \
                   --key-name CLD_KEY_DRUPAL_DEVOPSTEAM18 \
                   --private-ip-address 10.0.18.140 \
                   --security-group-ids sg-060333a9f2656e446 sg-059f0b49f5ca4aab8 \
                   --subnet-id subnet-04a2fc4d8de790824 \
                   --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_B}]'

[OUTPUT]
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
//updated string connection
```

## Check SQL Accesses

```sql
[INPUT]
//sql string connection from A

[OUTPUT]
```

```sql
[INPUT]
//sql string connection from B

[OUTPUT]
```

### Check HTTP Accesses

```bash
//connection string updated
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
//TODO
```

### Change the profil picture

* Observations ?

```
//TODO
```