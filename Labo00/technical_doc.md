# CLD - LABO 00

Objectifs:

* Selon le schéma

---

## Création des éléments communs de l'infrastructure

Il s'agit de déployer:
* Le VPC permettant d'isoler notre infrastructure intégrant une passerelle internet (Internet Gateway).
* Le réseau public qui jouera le rôle de DMZ comprenant:
    * Un serveur SSH offrant l'accès à l'admin système pour gérer les différentes instances
    * Un serveur NAT offrant le service de translation d'adresse nécessaire pour permettre aux instances d'accéder au net.

![Infra](./img/CLD_AWS_INFA.PNG)

---

## Prérequis

Pour réaliser ce laboratoire, il est nécessaire d'avoir installé et initialiser le CLI d'AWS. La version testée durant la rédaction de ce document est la suivante :

```
aws --version
```

```
aws-cli/2.8.3 Python/3.9.11 Windows/10 exe/AMD64 prompt/off
```

Note 1 : il est possible de travailler directement sur le serveur SSH pour réaliser les commandes. L'instance en question intègre déjà le CLI dont nous avons besoin.

Note 2 : les *credentials* dont vous avez besoin ont été mis à disposition dans vos canaux teams privés respectifs.

Note 3 : ces commandes ont été réalisées et validées avec les outils suivants:
* 

Important : seule la région milanaise vous autorise à créer des "composants" nécessaires à ce laboratoire (eu-south-1).

---

## Réalisation

### CREATE VPC

[Documentation AWS - VPC](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-vpc.html)

[INPUT]
```
aws ec2 create-vpc ^
    --cidr-block 10.0.0.0/16 ^
    --tag-specification ResourceType=vpc,Tags=[{Key=Name,Value=VPC-CLD}] ^ --region eu-south-1
```

[OUTPUT]
```
{
    "Vpc": {
        "CidrBlock": "10.0.0.0/16",
        "DhcpOptionsId": "dopt-1b957672",
        "State": "pending",
        "VpcId": "vpc-01b06683e72bf17be",
        "OwnerId": "709024702237",
        "InstanceTenancy": "default",
        "Ipv6CidrBlockAssociationSet": [],
        "CidrBlockAssociationSet": [
            {
                "AssociationId": "vpc-cidr-assoc-050eb38166d87a2e3",
                "CidrBlock": "10.0.0.0/16",
                "CidrBlockState": {
                    "State": "associated"
                }
            }
        ],
        "IsDefault": false,
        "Tags": [
            {
                "Key": "Name",
                "Value": "VPC-CLD"
            }
        ]
    }
}
```

### CREATE IGW

[Documentation AWS - IGW](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-internet-gateway.html)

[INPUT]
```
aws ec2 create-internet-gateway ^
    --tag-specifications ResourceType=internet-gateway,Tags=[{Key=Name,Value=IGW-CLD}] ^
    --region eu-south-1
```

[OUTPUT]
```
{
    "InternetGateway": {
        "Attachments": [],
        "InternetGatewayId": "igw-05d9166c4245072f8",
        "OwnerId": "709024702237",
        "Tags": [
            {
                "Key": "Name",
                "Value": "IGW-CLD"
            }
        ]
    }
}
```

### ATTACH IGW

[Documentation AWS - Attach IGW](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/attach-internet-gateway.html)

[INPUT]
```
aws ec2 attach-internet-gateway ^
    --internet-gateway-id igw-05d9166c4245072f8 ^
    --vpc-id vpc-0d210221771d593a0 ^
    --region eu-south-1
```

[OUTPUT]
```
[none]
```

[Documentation AWS - Describe IGWs](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/describe-internet-gateways.html)

[INPUT]
```
aws ec2 describe-internet-gateways --output table --region eu-south-1
```

[OUTPUT]
```
---------------------------------------------
|         DescribeInternetGateways          |
+-------------------------------------------+
||            InternetGateways             ||
|+------------------------+----------------+|
||    InternetGatewayId   |    OwnerId     ||
|+------------------------+----------------+|
||  igw-04e167845c16e593e |  709024702237  ||
|+------------------------+----------------+|
|||              Attachments              |||
||+------------+--------------------------+||
|||    State   |          VpcId           |||
||+------------+--------------------------+||
|||  available |  vpc-0ae930118f4e3cf79   |||
||+------------+--------------------------+||
|||                 Tags                  |||
||+---------------+-----------------------+||
|||      Key      |         Value         |||
||+---------------+-----------------------+||
|||  Name         |  CLD-IGW              |||
||+---------------+-----------------------+||
||            InternetGateways             ||
|+------------------------+----------------+|
||    InternetGatewayId   |    OwnerId     ||
|+------------------------+----------------+|
||  igw-05d9166c4245072f8 |  709024702237  ||
|+------------------------+----------------+|
|||              Attachments              |||
||+------------+--------------------------+||
|||    State   |          VpcId           |||
||+------------+--------------------------+||
|||  available |  vpc-0d210221771d593a0   |||
||+------------+--------------------------+||
|||                 Tags                  |||
||+---------------+-----------------------+||
|||      Key      |         Value         |||
||+---------------+-----------------------+||
|||  Name         |  IGW-CLD              |||
||+---------------+-----------------------+||
```

### CREATE SUBNET

[AWS Documentation - Create a subnet](https://docs.aws.amazon.com/vpc/latest/userguide/create-subnets.html)

[INPUT]

```bash
aws ec2 create-subnet \
    --vpc-id vpc-03d46c285a2af77ba \
    --cidr-block 10.0.18.0/28 \
    --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=SUB-DEVOPSTEAM18}]' \
    --availability-zone eu-west-3a
```

[OUTPUT]

```json
{
    "Subnet": {
        "AvailabilityZone": "eu-west-3a",
        "AvailabilityZoneId": "euw3-az1",
        "AvailableIpAddressCount": 11,
        "CidrBlock": "10.0.18.0/28",
        "DefaultForAz": false,
        "MapPublicIpOnLaunch": false,
        "State": "available",
        "SubnetId": "subnet-09242c27b33c8ded4",
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237",
        "AssignIpv6AddressOnCreation": false,
        "Ipv6CidrBlockAssociationSet": [],
        "Tags": [
            {
                "Key": "Name",
                "Value": "SUB-DEVOPSTEAM18"
            }
        ],
        "SubnetArn": "arn:aws:ec2:eu-west-3:709024702237:subnet/subnet-09242c27b33c8ded4",
        "EnableDns64": false,
        "Ipv6Native": false,
        "PrivateDnsNameOptionsOnLaunch": {
            "HostnameType": "ip-name",
            "EnableResourceNameDnsARecord": false,
            "EnableResourceNameDnsAAAARecord": false
        }
    }
}
```

The above command successfully created a subnet in the VPC with the ID `vpc-03d46c285a2af77ba` and the CIDR block `10.0.18.0/28` on the availability zone `eu-west-3a`.

### CREATE ROUTE TABLE

[INPUT]

```bash
aws ec2 create-route-table \
                             --vpc-id vpc-03d46c285a2af77ba \
                             --region eu-west-3 \
                             --dry-run
```

[OUTPUT]

```json
{
    "RouteTable": {
        "Associations": [],
        "PropagatingVgws": [],
        "RouteTableId": "rtb-02b205041756bb30e",
        "Routes": [
            {
                "DestinationCidrBlock": "10.0.0.0/16",
                "GatewayId": "local",
                "Origin": "CreateRouteTable",
                "State": "active"
            }
        ],
        "Tags": [],
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237"
    },
    "ClientToken": "3a27037c-7472-471e-8a09-40c35b767c59"
}
```

### ASSOCIATE ROUTE TABLE TO SUBNET

[INPUT]

```bash
aws ec2 associate-route-table \
    --route-table-id rtb-02b205041756bb30e\
    --subnet-id subnet-09242c27b33c8ded4 \
    --dry-run
```

[OUTPUT]

```json
{
    "AssociationId": "rtbassoc-07ec422161e54ec71",
    "AssociationState": {
        "State": "associated"
    }
}
```

### CREATE ROUTES

[INPUT]

```bash

```

[OUTPUT]

```json

```

### CREATE SECURITY GROUP

[INPUT]

```bash
aws ec2 create-security-group \
    --group-name SG-DEVOPSTEAM18 \
    --description "Allow ports 22 and 8080" \
    --vpc-id vpc-03d46c285a2af77ba \
    --tag-specifications 'ResourceType=security-group,Tags=[{Key=Name,Value=SG-DEVOPSTEAM18}]'
```

[OUTPUT]

```json
{
    "GroupId": "sg-06a717f4716484e02"
}
```

### ADD SECURITY GROUP RULES

[INPUT]

```bash
aws ec2 authorize-security-group-ingress \
    --group-id sg-06a717f4716484e02 \
    --protocol tcp \
    --port 22
```

[OUTPUT]

```json
{
    "Return": true,
    "SecurityGroupRules": []
}
```

[INPUT]

```bash
aws ec2 authorize-security-group-ingress \
    --group-id sg-06a717f4716484e02 \
    --protocol tcp \
    --port 8080
```

[OUTPUT]

```json
{
    "Return": true,
    "SecurityGroupRules": []
}
```
