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

```

* Get the ELB FQDN (DNS NAME - A Record)

\[INPUT\]

```bash
aws elbv2 describe-load-balancers \
    --names ELB-DEVOPSTEAM18
```

\[OUTPUT\]

```json

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
