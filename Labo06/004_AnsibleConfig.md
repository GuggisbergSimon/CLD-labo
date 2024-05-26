# Task 4: Configure Ansible to connect to the managed VM

In this task you will tell Ansible about the machines it shall manage.

In the lab directory create a directory `ansible`. In this directory, create a file called
`hosts` which will serve as the inventory file with the following content (in these instructions we have broken the file contents into
multiple lines so that it fits on the page, but it should be all on
one line in your file, without any backslashes):

    gce_instance ansible_ssh_host=<managed VM's public IP address> \
      ansible_ssh_user=<the username to access the VM> \
      ansible_ssh_private_key_file=<path to the private SSH key to access the VM>
      
Replace the fields marked with `< >` with your values.

Verify that you can use Ansible to connect to the server:

```bash
ansible gce_instance -i hosts -m ping
```

You should see output similar to the following:

```json
gce_instance | SUCCESS => {
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/bin/python3"
    },
    "changed": false,
    "ping": "pong"
}
```

We can now simplify the configuration of Ansible by using an
`ansible.cfg` file which allows us to set some defaults.

In the _ansible_ directory create the file `ansible.cfg`:

    [defaults]
    inventory = hosts
    remote_user = <the username to access the VM>
    private_key_file = <path to the private SSH key to access the VM>
    host_key_checking = false
    deprecation_warnings = false

Among the default options we also disable SSH's host key
checking. This is convenient when we destroy and recreate the managed
server (it will get a new host key every time). In production this may
be a security risk.

We also disable warnings about deprecated features that Ansible emits.

With these default values the `hosts` inventory file now simplifies to:

```bash
gce_instance ansible_ssh_host=<managed VM's public IP address>
```

We can now run Ansible again and don't need to specify the inventory
file any more:

```bash
ansible gce_instance -m ping
```

The `ansible` command can be used to run arbitrary commands on the
remote machines. Use the `-m command` option and add the command in
the `-a` option. For example to execute the `uptime` command:

```bash
ansible gce_instance -m command -a uptime
```

You should see output similar to this:

```bash
gce_instance | CHANGED | rc=0 >>
    18:56:58 up 25 min,  1 user,  load average: 0.00, 0.01, 0.02
```

Deliverables:

- What happens if the infrastructure is deleted and then recreated with Terraform? What needs to be updated to access the infrastructure again?

**\[INPUT\]**
```
//terraform destroy command
terraform destroy
```

**\[OUTPUT\]**
```
google_compute_firewall.http: Refreshing state... [id=projects/labgce-424207/global/firewalls/allow-http]
google_compute_firewall.ssh: Refreshing state... [id=projects/labgce-424207/global/firewalls/allow-ssh]
google_compute_instance.default: Refreshing state... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0]

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  - destroy

Terraform will perform the following actions:

# Removed for brevity

Plan: 0 to add, 0 to change, 3 to destroy.

Changes to Outputs:
  - gce_instance_ip = "34.65.222.7" -> null

Do you really want to destroy all resources?
  Terraform will destroy all your managed infrastructure, as shown above.
  There is no undo. Only 'yes' will be accepted to confirm.

  Enter a value: yes

google_compute_firewall.http: Destroying... [id=projects/labgce-424207/global/firewalls/allow-http]
google_compute_firewall.ssh: Destroying... [id=projects/labgce-424207/global/firewalls/allow-ssh]
google_compute_instance.default: Destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0]
google_compute_instance.default: Still destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0, 10s elapsed]
google_compute_firewall.http: Still destroying... [id=projects/labgce-424207/global/firewalls/allow-http, 10s elapsed]
google_compute_firewall.ssh: Still destroying... [id=projects/labgce-424207/global/firewalls/allow-ssh, 10s elapsed]
google_compute_firewall.http: Destruction complete after 11s
google_compute_firewall.ssh: Destruction complete after 11s
google_compute_instance.default: Still destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0, 20s elapsed]
google_compute_instance.default: Still destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0, 30s elapsed]
google_compute_instance.default: Still destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0, 40s elapsed]
google_compute_instance.default: Still destroying... [id=projects/labgce-424207/zones/europe-west6-a/instances/gce-srv-0, 50s elapsed]
google_compute_instance.default: Destruction complete after 51s

Destroy complete! Resources: 3 destroyed.
```

Recreate the infra (no input/output needed)

```txt
When deleting the infrastructure and recreating it, the IP address of the managed VM will change. Note that they are both on the `34.65.x.x` subnet, but the last octet will change. To access the infrastructure again, the `hosts` file in the `ansible` directory will need to be updated with the new IP address.
```