# Deploy Instance

## Intention
In this part of the lab, we're going to deploy a first ec2 instance on our private subnet.

In order to respect the constraints of our infrastructure, we'll need to modify certain elements of our instance so that we can access it via ssh as well as via the web browser.

### Prerequisites

* Retrieve the private key dedicated to the Drupal machine delivered in your private team channel (folder AWS).
* Study the [AWS CLI documentation to identify the constraints to be met before we can deploy our Drupal instance](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/run-instances.html).
* Refer to our infrastructure schema to understand how we can expose/access our application.

![CLD AWS INFRA](./img/CLD_AWS_INFA.PNG)

---

### Task 01 - Network settings

* [Create your subnet](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-subnet.html)

|Key|Value|
|:--|:--|
|Name|SUB-PRIVATE-DEVOPSTEAM<XX>|
|Vpc-id|vpc-03d46c285a2af77ba|
|IPv4 CIDR block|10.0.<XX>.0/28|
|Availability Zone|eu-west-3a|

* [Create your route table](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-route-table.html)

|Key|Value|
|:--|:--|
|Name|RTBLE-PRIVATE-DRUPAL-DEVOPSTEAM<XX>|
|Subnet Association|With your private subnet|

* [Create your routes](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-route.html)

Note : Refer to the infra schema to add.

---

### Task 02 - Instance settings

* [Create your key pair](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-key-pair.html)

Note : already done (team channel -> files/aws)

* [Create your security group](https://docs.aws.amazon.com/cli/latest/userguide/cli-services-ec2-sg.html)

|Key|Value|
|:--|:--|
|Name|SG-PRIVATE-DRUPAL-DEVOPSTEAM<xx>|
|Outbound rules|Refer to infra schema|
|Outbound rules|Refer to infra schema|

* Update the NAT Security group

Note : Refer to the infra schema to add mandatory inbound rules for your subnet.

---

### Task 03 - Deploy Bitnami/Drupal Instance

* [Launch your instance](https://docs.aws.amazon.com/cli/latest/userguide/cli-services-ec2-instances.html)

|Key|Value|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM<XX>|
|AMI|ami-00b3a1b7cfab20134|
|Subnet|your private subnet|
|Key|your key|
|Instance type|t3.micro|
|Storage|1x10 Go gp2|

* [Get official doc](https://bitnami.com/support/aws)

```
aws ec2 run-instances \
    --image-id ami-00b3a1b7cfab20134 \
    --count 1 \
    --instance-type t3.micro \
    --key-name CLD_KEY_DRUPAL_DEVOPSTEAM18 \
    --security-group-ids sg-060333a9f2656e446 \
    --subnet-id subnet-0d395759a91c4d4b8 \
    --block-device-mappings 'DeviceName=/dev/sda1,Ebs={VolumeSize=10}' \
    --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM18}]'
```

[OUTPUT]

```json
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-00b3a1b7cfab20134",
            "InstanceId": "i-04f6ffaec28f2162c",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM18",
            "LaunchTime": "2024-03-07T16:04:23+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3a",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-18-12.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.18.12",
            "ProductCodes": [],
            "PublicDnsName": "",
                        "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-0d395759a91c4d4b8",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "2286c13c-1317-4260-a77a-8664368b8da3",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-07T16:04:23+00:00",
                        "AttachmentId": "eni-attach-0cf225747c5b173d3",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                                            },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM18",
                            "GroupId": "sg-060333a9f2656e446"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "06:aa:e2:89:5b:eb",
                    "NetworkInterfaceId": "eni-030724b67f5b8ebf8",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.18.12",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.18.12"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-0d395759a91c4d4b8",
                                        "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
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
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM18"
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
    "ReservationId": "r-0b468a21f0e1109de"
}
```

---

### Task 04 - SSH Access to your private Drupal Instance

* Update your ssh string connection adding a ssh tunnel to your Drupal instance

```bash
[your connection string] -L <localPortToForward>:<privateIpOfYouDrupal>:22

//Sample
ssh devopsteam<xx>@15.188.43.46 -i CLD_KEY_DMZ_DEVOPSTEAM<xx>.pem -L 2223:10.0.<xx>.10:22
```

[INPUT]

```bash
ssh devopsteam18@15.188.43.46 \
    -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem \
    -NvL 1337:10.0.18.12:22 \
    -o IdentitiesOnly=true
```

* Create the connection string to your drupal instance

```
<user>@localhost -i <pathToYourDrupalSSHKey>

//Sample
ssh bitnami@localhost -p 2223 -i CLD_KEY_DRUPAL_DEVOPSTEAM<xx>.pem
```

[INPUT]

```bash
ssh bitnami@localhost -p 1337 \
    -i .ssh/CLD_KEY_DRUPAL_DEVOPSTEAM18.pem \
    -o IdentitiesOnly=true
```

* Renew your ssh connection first, then launch a second ssh session for your Drupal instance

* Result expected

```
Linux ip-10-0-<xx>-10 5.10.0-28-cloud-amd64 #1 SMP Debian 5.10.209-2 (2024-01-31) x86_64

The programs included with the Debian GNU/Linux system are free software;
the exact distribution terms for each program are described in the
individual files in /usr/share/doc/*/copyright.

Debian GNU/Linux comes with ABSOLUTELY NO WARRANTY, to the extent
permitted by applicable law.
       ___ _ _                   _
      | _ |_) |_ _ _  __ _ _ __ (_)
      | _ \ |  _| ' \/ _` | '  \| |
      |___/_|\__|_|_|\__,_|_|_|_|_|

  *** Welcome to the Bitnami package for Drupal 10.2.3-1        ***
  *** Documentation:  https://docs.bitnami.com/aws/apps/drupal/ ***
  ***                 https://docs.bitnami.com/aws/             ***
  *** Bitnami Forums: https://github.com/bitnami/vms/           ***
Last login: Wed Mar  6 18:43:11 2024 from 10.0.0.5
```

---

### Task 05 - Web access to your private Drupal Instance

#### INSIDE THE SUBNET

* Test directly on the ssh srv (inside the private subnet)

```
[INPUT]
curl localhost

[OUTPUT]
you get the html content of the home page
```

* Change the default port of apache

```
file : /opt/bitnami/apache2/conf/httpd.conf
LISTEN 8080
```

```
file : /opt/bitnami/apache2/conf/bitnami/bitnami.conf
<VirtualHost _default_:8080>
```

```
file : /opt/bitnami/apache2/conf/vhosts/
<VirtualHost 127.0.0.1:8080 _default_:8080>
```

* Restart Apache Server

```
sudo /opt/bitnami/ctlscript.sh restart apache
```

#### FROM THE DMZ

* Test directly on the ssh srv (outside the private subnet)

```
[INPUT]
curl localhost

[OUTPUT]
you get the html content of the home page
```

#### FROM THE WEB (THROUGH SSH)

* Update your ssh string connection adding a http tunnel to your Drupal instance

```bash
[your connection string] -L <localPortToForward>:<privateIpOfYouDrupal>:22

//Sample
ssh devopsteam<xx>@15.188.43.46 -i CLD_KEY_DMZ_DEVOPSTEAM<xx>.pem -L 2223:10.0.<xx>.10:22 -L 888:10.0.<xx>.10:8080
```

[INPUT]

```bash
ssh devopsteam18@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem -NvL 8888:10.0.18.12:8080
```

* Test directly on your localhost, using your browser

```bash
curl localhost:8888
```

![DRUPAL_HOME_PAGE](./img/DRUPAL_HOME_PAGE.PNG)

#### STOP THE INSTANCE

```bash
aws ec2 stop-instances --instance-ids i-04f6ffaec28f2162c
```

#### CREATE AMI FROM INSTANCE

```bash
aws ec2 create-image \
    --instance-id i-04f6ffaec28f2162c \
    --name "AMI_PRIVATE_DRUPAL_DEVOPSTEAM18" \
    --description "Drupal AMI for week lab1/week3"
```

[OUTPUT]

```json
{
    "ImageId": "ami-058adb4d984c76f24"
}
```

#### TERMINATE THE INSTANCE

```bash
aws ec2 terminate-instances --instance-ids i-04f6ffaec28f2162c
```
