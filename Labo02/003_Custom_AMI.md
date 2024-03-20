# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in 
the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

| Key         | Value for GUI Only                 |
| :---------- | :--------------------------------- |
| Name        | AMI_DRUPAL_DEVOPSTEAM18_LABO02_RDS |
| Description | Same as name value                 |

\[INPUT\]

```bash
aws ec2 stop-instances --instance-id i-08b03e25dbfb38598

aws ec2 create-image \
    --instance-id i-08b03e25dbfb38598 \
    --name "AMI_PRIVATE_DRUPAL_DEVOPSTEAM18_LABO02_RDS" \
    --description "AMI_PRIVATE_DRUPAL_DEVOPSTEAM18_LABO02_RDS" \
    --tag-specifications 'ResourceType=image,Tags=[{Key=Name,Value=AMI_PRIVATE_DRUPAL_DEVOPSTEAM18_LABO02_RDS}]'
```

\[OUTPUT\]

```json
{
    "ImageId": "ami-0a0dd8d0c93bcf9bc"
}
```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1

``

* Deploy Drupal Instance based on AMI in Az2

| Key         | Value for GUI Only                |
| :---------- | :-------------------------------- |
| Name        | EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_B |
| Description | Same as name value                |

\[INPUT\]

```bash
aws ec2 start-instances --instance-ids i-08b03e25dbfb38598

aws ec2 run-instances \
   --image-id ami-0a0dd8d0c93bcf9bc \
   --count 1 \
   --instance-type t3.micro \
   --key-name CLD_KEY_DRUPAL_DEVOPSTEAM18 \
   --private-ip-address 10.0.18.140 \
   --security-group-ids sg-060333a9f2656e446 sg-059f0b49f5ca4aab8 \
   --subnet-id subnet-04a2fc4d8de790824 \
   --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_B}]' \
   --placement AvailabilityZone=eu-west-3b
```

\[OUTPUT\]

```json
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-0a0dd8d0c93bcf9bc",
            "InstanceId": "i-073e9bed9d50cf8d8",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM18",
            "LaunchTime": "2024-03-20T14:53:43+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3b",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-18-140.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.18.140",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-04a2fc4d8de790824",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "9d954217-852f-4306-9e5f-6e2864da2202",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-20T14:53:43+00:00",
                        "AttachmentId": "eni-attach-05c15b8dd368a9c26",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                    },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM18-RDS",
                            "GroupId": "sg-059f0b49f5ca4aab8"
                        },
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM18",
                            "GroupId": "sg-060333a9f2656e446"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "0a:64:83:a9:8b:47",
                    "NetworkInterfaceId": "eni-07c69d11ec63285db",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.18.140",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.18.140"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-04a2fc4d8de790824",
                    "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM18-RDS",
                    "GroupId": "sg-059f0b49f5ca4aab8"
                },
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM18",
                    "GroupId": "sg-060333a9f2656e446"
                }
            ],
            "SourceDestCheck": true,
            "StateReason": {
                "Code": "pending",
                "Message": "pending"
            },
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM18_B"
                }
            ],
            "VirtualizationType": "hvm",
            "CpuOptions": {
                "CoreCount": 1,
                "ThreadsPerCore": 2
            },
            "CapacityReservationSpecification": {
                "CapacityReservationPreference": "open"
            },
            "MetadataOptions": {
                "State": "pending",
                "HttpTokens": "optional",
                "HttpPutResponseHopLimit": 1,
                "HttpEndpoint": "enabled",
                "HttpProtocolIpv6": "disabled",
                "InstanceMetadataTags": "disabled"
            },
            "EnclaveOptions": {
                "Enabled": false
            },
            "PrivateDnsNameOptions": {
                "HostnameType": "ip-name",
                "EnableResourceNameDnsARecord": false,
                "EnableResourceNameDnsAAAARecord": false
            },
            "MaintenanceOptions": {
                "AutoRecovery": "default"
            },
            "CurrentInstanceBootMode": "legacy-bios"
        }
    ],
    "OwnerId": "709024702237",
    "ReservationId": "r-080fc1f2b1c2ce861"
}
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
ssh devopsteam18@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem -Nv \
    -L 1337:10.0.18.10:22 \
    -L 8080:10.0.18.10:8080 \
    -L 1338:10.0.18.140:22 \
    -L 8081:10.0.18.140:8080 \

# In different shells
ssh bitnami@localhost -p 1337 -i .ssh/CLD_KEY_DRUPAL_DEVOPSTEAM18.pem
ssh bitnami@localhost -p 1338 -i .ssh/CLD_KEY_DRUPAL_DEVOPSTEAM18.pem
```

## Check SQL Accesses

\[INPUT\]

```sql
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u bn_drupal -pa138798bc8d2f05ad6622993f87dad5684be936c93e9490df6bb5523bda52eba \
    bitnami_drupal -e "SHOW GRANTS for bn_drupal@'10.0.18.%';"
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

\[INPUT\]

```sql
mariadb -h dbi-devopsteam18.cshki92s4w5p.eu-west-3.rds.amazonaws.com \
    -u bn_drupal -pa138798bc8d2f05ad6622993f87dad5684be936c93e9490df6bb5523bda52eba \
    bitnami_drupal -e "SHOW GRANTS for bn_drupal@'10.0.18.%';"
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

### Check HTTP Accesses

```bash
curl -I http://localhost:8080
curl -I http://localhost:8081
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page
on the second and validated that they are communicating with the same db (rds).

* Observations ?

```txt
The changes are immediately reflected on the second webapp because both apps are
using the same database instance.
```

### Change the profil picture

* Observations ?

```txt
The profile is successfully displayed on the instance on which the change was made.

The second instance shows an empty thumbnail because the file is not available.
This happens because the uploaded image is not saved in the database but on the filesystem
of the instance on which the changed was made.

The instances do not share a filesystem (the fs was cloned when creating an instance
based on an AMI) and the image is hence unavailable on the second instance.
```

<!-- aws ec2 stop-instances --instance-ids i-08b03e25dbfb38598 i-073e9bed9d50cf8d8 -->
<!-- aws rds stop-db-instance --db-instance-identifier dbi-devopsteam18 -->
