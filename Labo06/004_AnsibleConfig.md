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

//TODO
[INPUT]
```
//terraform destroy command
```

[OUTPUT]
```
```

Recreate the infra (no input/output needed)

```
//TODO
```