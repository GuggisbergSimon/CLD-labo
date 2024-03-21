### Deploy the elastic load balancer

In this task you will create a load balancer in AWS that will receive
the HTTP requests from clients and forward them to the Drupal
instances.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 Prerequisites for the ELB

* Create a dedicated security group

| Key            | Value                     |
| :------------- | :------------------------ |
| Name           | SG-DEVOPSTEAM[XX]-LB      |
| Inbound Rules  | Application Load Balancer |
| Outbound Rules | Refer to the infra schema |

\[INPUT\]

```bash
aws ec2 create-security-group \
    --group-name SG-DEVOPSTEAM18-LB \
    --description "Allow load balancer traffic" \
    --vpc-id vpc-03d46c285a2af77ba \
    --tag-specifications 'ResourceType=security-group,Tags=[{Key=Name,Value=SG-DEVOPSTEAM18-LB}]'
```

\[OUTPUT\]

```json
{
    "GroupId": "sg-0d7bbbdb111abe4b4",
    "Tags": [
        {
            "Key": "Name",
            "Value": "SG-DEVOPSTEAM18-LB"
        }
    ]
}
```

\[INPUT\]

```bash
aws ec2 authorize-security-group-ingress \
    --group-id sg-0d7bbbdb111abe4b4 \
    --ip-permissions IpProtocol=tcp,FromPort=8080,ToPort=8080,IpRanges='[{CidrIp=0.0.0.0/0, Description="Allow HTTP from DMZ"}]' \
    --tag-specifications 'ResourceType=security-group-rule,Tags=[{Key=Name,Value=HTTP-ALLOW}, {Key=Description, Value=Allow HTTP from DMZ}]'
```

\[OUTPUT\]

```json
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0ed6a31474976b255",
            "GroupId": "sg-0d7bbbdb111abe4b4",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "0.0.0.0/0",
            "Description": "Allow HTTP from DMZ",
            "Tags": [
                {
                    "Key": "Description",
                    "Value": "Allow HTTP from DMZ"
                },
                {
                    "Key": "Name",
                    "Value": "HTTP-ALLOW"
                }
            ]
        }
    ]
}
```

* Create the Target Group

[Source](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-target-groups.html)

| Key                   | Value                                |
| :-------------------- | :----------------------------------- |
| Target type           | Instances                            |
| Name                  | TG-DEVOPSTEAM[XX]                    |
| Protocol and port     | Refer to the infra schema            |
| Ip Address type       | IPv4                                 |
| VPC                   | Refer to the infra schema            |
| Protocol version      | HTTP1                                |
| Health check protocol | HTTP                                 |
| Health check path     | /                                    |
| Port                  | Traffic port                         |
| Healthy threshold     | 2 consecutive health check successes |
| Unhealthy threshold   | 2 consecutive health check failures  |
| Timeout               | 5 seconds                            |
| Interval              | 10 seconds                           |
| Success codes         | 200                                  |

\[INPUT\]

```bash
aws elbv2 create-target-group \
    --name TG-DEVOPSTEAM18 \
    --protocol HTTP \
    --protocol-version HTTP1 \
    --port 8080 \
    --vpc-id vpc-03d46c285a2af77ba \
    --health-check-protocol HTTP \
    --health-check-path / \
    --health-check-port 8080 \
    --health-check-interval-seconds 10 \
    --health-check-timeout-seconds 5 \
    --healthy-threshold-count 2 \
    --unhealthy-threshold-count 2 \
    --target-type instance \
    --matcher HttpCode=200 \
    --tags 'Key=Name,Value=TG-DEVOPSTEAM18'
```

\[OUTPUT\]

```json
{
    "TargetGroups": [
        {
            "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM18/99ff61700d72e152",
            "TargetGroupName": "TG-DEVOPSTEAM18",
            "Protocol": "HTTP",
            "Port": 8080,
            "VpcId": "vpc-03d46c285a2af77ba",
            "HealthCheckProtocol": "HTTP",
            "HealthCheckPort": "8080",
            "HealthCheckEnabled": true,
            "HealthCheckIntervalSeconds": 10,
            "HealthCheckTimeoutSeconds": 5,
            "HealthyThresholdCount": 2,
            "UnhealthyThresholdCount": 2,
            "HealthCheckPath": "/",
            "Matcher": {
                "HttpCode": "200"
            },
            "TargetType": "instance",
            "ProtocolVersion": "HTTP1",
            "IpAddressType": "ipv4"
        }
    ]
}
```


## Task 02 Deploy the Load Balancer

[Source](https://aws.amazon.com/elasticloadbalancing/)

* Create the Load Balancer

| Key                         | Value                                    |
| :-------------------------- | :--------------------------------------- |
| Type                        | Application Load Balancer                |
| Name                        | ELB-DEVOPSTEAM99                         |
| Scheme                      | Internal                                 |
| Ip Address type             | IPv4                                     |
| VPC                         | Refer to the infra schema                |
| Security group              | Refer to the infra schema                |
| Listeners Protocol and port | Refer to the infra schema                |
| Target group                | Your own target group created in task 01 |

Provide the following answers (leave any field not mentioned at its default value):

\[INPUT\]

```bash
aws elbv2 create-load-balancer \
    --name ELB-DEVOPSTEAM18 \
    --subnets subnet-0bd3b8cdf25b8042e subnet-04a2fc4d8de790824 \
    --security-groups sg-0d7bbbdb111abe4b4 \
    --scheme internal \
    --type application \
    --ip-address-type ipv4 \
    --tags 'Key=Name,Value=ELB-DEVOPSTEAM18'
```

\[OUTPUT\]

```json
{
    "LoadBalancers": [
        {
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM18/f62cf8f19f5a69ea",
            "DNSName": "internal-ELB-DEVOPSTEAM18-1198556003.eu-west-3.elb.amazonaws.com",
            "CanonicalHostedZoneId": "Z3Q77PNBQS71R4",
            "CreatedTime": "2024-03-21T14:51:19.290000+00:00",
            "LoadBalancerName": "ELB-DEVOPSTEAM18",
            "Scheme": "internal",
            "VpcId": "vpc-03d46c285a2af77ba",
            "State": {
                "Code": "provisioning"
            },
            "Type": "application",
            "AvailabilityZones": [
                {
                    "ZoneName": "eu-west-3b",
                    "SubnetId": "subnet-04a2fc4d8de790824",
                    "LoadBalancerAddresses": []
                },
                {
                    "ZoneName": "eu-west-3a",
                    "SubnetId": "subnet-0bd3b8cdf25b8042e",
                    "LoadBalancerAddresses": []
                }
            ],
            "SecurityGroups": [
                "sg-0d7bbbdb111abe4b4"
            ],
            "IpAddressType": "ipv4"
        }
    ]
}
```

* Get the ELB FQDN (DNS NAME - A Record)

\[INPUT\]

```bash
aws elbv2 describe-load-balancers \
    --names ELB-DEVOPSTEAM18 \
    | jq '.LoadBalancers[0].DNSName'
```

\[OUTPUT\]

```json
"internal-ELB-DEVOPSTEAM18-1198556003.eu-west-3.elb.amazonaws.com"
```

Register the Target Group for the two instances

\[INPUT\]
```bash
aws elbv2 register-targets \
--target-group-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM18/99ff61700d72e152 \
--targets Id=i-08b03e25dbfb38598 Id=i-073e9bed9d50cf8d8
```

\[INPUT\]
```bash
aws elbv2 create-listener \
--load-balancer-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM18/f62cf8f19f5a69ea \
--protocol HTTP \
--port 8080 \
--default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM18/99ff61700d72e152
```

\[OUTPUT\]
```json
{
    "Listeners": [
        {
            "ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-DEVOPSTEAM18/f62cf8f19f5a69ea/7fd344d31b6d4f15",
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM18/f62cf8f19f5a69ea",
            "Port": 8080,
            "Protocol": "HTTP",
            "DefaultActions": [
                {
                    "Type": "forward",
                    "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM18/99ff61700d72e152",
                    "ForwardConfig": {
                        "TargetGroups": [
                            {
                                "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM18/99ff61700d72e152",
                                "Weight": 1
                            }
                        ],
                        "TargetGroupStickinessConfig": {
                            "Enabled": false
                        }
                    }
                }
            ]
        }
    ]
}
```

No output from this command.

Modify the SG-PRIVATE-DRUPAL-DEVOPSTEAM18 security group to allow traffic
from the sg-060333a9f2656e446 security group.

\[INPUT\]
```bash
aws ec2 authorize-security-group-ingress \
    --group-id sg-060333a9f2656e446 \
    --protocol tcp \
    --port 8080 \
    --source-group sg-0d7bbbdb111abe4b4
```

\[OUTPUT\]

```json
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-05f3b378a1bd8fe54",
            "GroupId": "sg-060333a9f2656e446",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "ReferencedGroupInfo": {
                "GroupId": "sg-0d7bbbdb111abe4b4",
                "UserId": "709024702237"
            }
        }
    ]
}
```

* Get the ELB deployment status

Note : In the EC2 console select the Target Group. In the
       lower half of the panel, click on the **Targets** tab. Watch the
       status of the instance go from **unused** to **initial**.

* Ask the DMZ administrator to register your ELB with the reverse proxy via the
private teams channel

* Update your string connection to test your ELB and test it

```bash
// Updated connection string
ssh devopsteam18@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM18.pem -Nv \
    -L 8080:[ELB DNS NAME]:80
```

* Test your application through your ssh tunneling

\[INPUT\]

```bash
curl localhost:[local port forwarded]

```

\[OUTPUT\]

```json

```

### Questions - Analysis

* On your local machine resolve the DNS name of the load balancer into
  an IP address using the `nslookup` command (works on Linux, macOS and Windows). Write
  the DNS name and the resolved IP Address(es) into the report.

```
//TODO
```

* From your Drupal instance, identify the ip from which requests are sent by the Load Balancer.

Help : execute `tcpdump port 8080`

```
//TODO
```

* In the Apache access log identify the health check accesses from the
  load balancer and copy some samples into the report.

```
//TODO
```
