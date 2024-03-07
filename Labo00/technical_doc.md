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
    --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=SUB-PRIVATE-DEVOPSTEAM18}]'
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
        "SubnetId": "subnet-0d395759a91c4d4b8",
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237",
        "AssignIpv6AddressOnCreation": false,
        "Ipv6CidrBlockAssociationSet": [],
        "Tags": [
            {
                "Key": "Name",
                "Value": "SUB-PRIVATE-DEVOPSTEAM18"
            }
        ],
        "SubnetArn": "arn:aws:ec2:eu-west-3:709024702237:subnet/subnet-0d395759a9$
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

### CREATE ROUTE TABLE

[INPUT]

```bash
aws ec2 create-route-table \
    --vpc-id vpc-03d46c285a2af77ba \
    --region eu-west-3 \
    --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=RTBLE-PRIVATE-DRUPAL-DEVOPSTEAM18}]'
```

[OUTPUT]

```json
{
    "RouteTable": {
        "Associations": [],
        "PropagatingVgws": [],
        "RouteTableId": "rtb-095da131e24ac8d10",
        "Routes": [
            {
                "DestinationCidrBlock": "10.0.0.0/16",
                "GatewayId": "local",
                "Origin": "CreateRouteTable",
                "State": "active"
            }
        ],
        "Tags": [
            {
                "Key": "Name",
                "Value": "RTBLE-PRIVATE-DRUPAL-DEVOPSTEAM18"
            }
        ],
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237"
    },
    "ClientToken": "bcbe9622-d0de-45f8-a2c2-a0ddc15e819a"
}
```

### ASSOCIATE ROUTE TABLE TO SUBNET

[INPUT]

```bash
aws ec2 associate-route-table \
    --route-table-id rtb-095da131e24ac8d10 \
    --subnet-id subnet-0d395759a91c4d4b8 \
```

[OUTPUT]

```json
{
    "AssociationId": "rtbassoc-0db6acfdbad9f5ff5",
    "AssociationState": {
        "State": "associated"
    }
}
```

### CREATE ROUTES

[INPUT]

```bash
aws ec2 create-route \
    --route-table-id rtb-02b205041756bb30e \
    --destination-cidr-block 0.0.0.0/0 \
    --instance-id i-085f07b949466919e \
    --dry-run
```

[OUTPUT]

```bash
{
    "Return": true
}
```

### CREATE SECURITY GROUP

[INPUT]

```bash
aws ec2 create-security-group \
    --group-name SG-PRIVATE-DRUPAL-DEVOPSTEAM18 \
    --description "Allow ports 22 and 8080" \
    --vpc-id vpc-03d46c285a2af77ba \
    --tag-specifications 'ResourceType=security-group,Tags=[{Key=Name,Value=SG-DEVOPSTEAM18}]'
```

[OUTPUT]

```json
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
```
