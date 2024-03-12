# Labo 1 - Questions

## What is the smallest and the biggest instance type (in terms of virtual CPUs and memory) that you can choose from when creating an instance?

```txt
The smallest instance type is t2.nano with 1 vCPU and 0.5 GB of memory.
The biggest instance type are the u-24tb1.112xlarge and u-24tb1.metal with 448
vCPUs and 24 TB of memory.
```

Sources:

- [Instance types](https://aws.amazon.com/ec2/instance-types/)
- [High Memory Instances](https://aws.amazon.com/ec2/instance-types/high-memory/)

## How long did it take for the new instance to get into the _running_ state?

```txt
It took about 15 to 20 seconds. Note that this metric may vary depending on the 
region,  the instance type, the OS (Linux is faster to set up than Windows) as 
well as EC2's current load
```

Sources:
[EC2 Instance lifecycle](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-instance-lifecycle.html)

Good read:

- [EC2 Boot Time Benchmarking](https://www.daemonology.net/blog/2021-08-12-EC2-boot-time-benchmarking.html)

## What's the difference between time here in Switzerland and the time set on the machine?

Using the commands to explore the machine listed earlier, respond to the
following questions and explain how you came to the answer:

```txt
On Linux, the time zone can be querried by running either `date +%Z` or `timedatectl`.

Running the aforementioned commands on our local host, we get 'CET'.
Running the command on the EC2 instance, we get 'UTC'.

All Linux instances are set to UTC time by default. Depending on the season, Switzerland
uses CET or CEST. This translate to a 1 to 2 hours difference between the instance's
time and the local time.
```

Sources:

- [Set the time for your Linux instance](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/set-time.html)
- [Current system time zone](https://www.baeldung.com/linux/current-system-time-zone)

## What's the name of the hypervisor?

```txt
By looking at the output of dmesg on the machine, we get :
bitnami@ip-10-0-18-8:~$ sudo dmesg | egrep 'DMI|Hypervisor'
[    0.000000] DMI: Amazon EC2 t3.micro/, BIOS 1.0 10/16/2017
[    0.000000] Hypervisor detected: KVM

Using the CLI, we get:
`aws ec2 describe-instance-types --instance-types t3.micro | jq ".InstanceTypes[0].Hypervisor"`
"nitro"

The machine uses the Nitro system which is a custom hypervisor developed by
AWS that leverages the KVM technology.
```

Sources:

- [What hypervisor is my linux VM running on](https://vcloudvision.com/2019/07/09/what-hypervisor-is-my-linux-vm-running-on/)
- [The Nitro System journey](https://docs.aws.amazon.com/whitepapers/latest/security-design-of-aws-nitro-system/the-nitro-system-journey.html)

## How much free space does the disk have?

```bash
bitnami@ip-10-0-18-8:~$ df -h --total | awk '/total/ {print $4}'
7.2G
```

## Try to ping the instance ssh srv from your local machine. What do you see?

Explain. Change the configuration to make it work. Ping the instance, record 5
round-trip times.

```txt
Ping from linux using `ping -O 15.188.43.46` displays "no answer yet [...]".
Ping from Windows displays "Request timed out".
```

## Determine the IP address seen by the operating system in the EC2 instance by running the `ifconfig` command.

What type of address is it?

Compare it to the address displayed by the ping command earlier. How do you
explain that you can successfully communicate with the machine?

```txt
sudo ifconfig ens5 | grep inet
        inet 10.0.18.8  netmask 255.255.255.240  broadcast 10.0.18.15
        inet6 fe80::43d:e4ff:fe83:250b  prefixlen 64  scopeid 0x20<link>

The IP address is part of the A Class private range. This means that the
address is meant to be used in a private network and is not directly 
available on the WAN.

Communication with the machine may only be established because a forwarded
SSH tunnel is set up towards the "SSH-Srv" machine, which can itself communicate
with the "Application Server" in the private subnet.
```
