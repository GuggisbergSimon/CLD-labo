### Task 5: Install a web server and configure a web site

In this task you will configure the managed host to run an NGINX web
server. This will necessitate four files:

* The inventory file from the previous task _(ansible/hosts)_.
* A playbook with instructions what to configure _(ansible/playbooks/web.yml)_.
* The configuration file for NGINX _(ansible/playbooks/files/nginx.conf)_.
* A template for the home page of our web site _(ansible/playbooks/templates/index.html.j2)_.

To make our playbook more generic, we will refer to our managed server
not by its individual name, but we will create a group called
_webservers_ and put our server into it. We can then later easily add
more servers which Ansible will configure identically.

Modify the file _ansible/hosts_ by adding a definition of the group
_webservers_, which for the time being contains exactly one server,
_gce_instance_:

    [webservers]
    gce_instance ansible_ssh_host=<your ip>

You should now be able to ping the webservers group:

```
ansible webservers -m ping
```

//TODO
[OUTPUT]
```json
```

The output should be the same as before.

Now get the [web.yml](./appendices/ansible/web.yml) playbook file and put it in the _ansible/playbooks_ directory.

The playbook references the configuration file for NGINX. Get the [nginx.conf](./appendices/ansible/nginx.conf) file and put it in the _ansible/playbooks/files_ directory.

The configuration file tells NGINX to serve the homepage from
_index.html_. We'll use Ansible's template functionality so that
Ansible will generate the file from a template.

Get the [index.html.j2](./appendices/ansible/index.html.j2) template file and put it in the _ansible/playbooks/templates_ directory.

You should have a file structure like this

    .
    ├── ansible
    │   ├── ansible.cfg
    │   ├── hosts
    │   └── playbooks
    │       ├── files
    │       │   └── nginx.conf
    │       ├── templates
    │       │   └── index.html.j2
    │       └── web.yml
    ├── credentials
    │   ├── labgce-service-account-key.json
    │   ├── labgce-ssh-key
    │   └── labgce-ssh-key.pub
    └── terraform
        ├── backend.tf
        ├── main.tf
        ├── outputs.tf
        ├── terraform.tfstate
        ├── terraform.tfstate.backup
        ├── terraform.tfvars
        └── variables.tf

Now you can run the newly created playbook to configure NGINX on the
managed host. The command to run playbooks is `ansible-playbook` and must be ran from the `ansible` directory:

```bash
ansible-playbook playbooks/web.yml
```

This should produce output similar to the following:

```bash
PLAY [Configure webservers with nginx] ******************************************

TASK [Gathering Facts] *********************************************************
ok: [gce_instance]

TASK [Install nginx] ***********************************************************
changed: [gce_instance]

TASK [Copy nginx config file] **************************************************
changed: [gce_instance]

TASK [Enable configuration] ****************************************************
ok: [gce_instance]

TASK [Copy index.html] *********************************************************
changed: [gce_instance]

TASK [Restart nginx] ***********************************************************
changed: [gce_instance]

PLAY RECAP *********************************************************************
gce_instance                 : ok=6    changed=4    unreachable=0    failed=0
```

You can then test the new web site by pointing your browser to the
address of the managed server. You should see the homepage showing
"NGINX, configured by Ansible".

//TODO
[INPUT]
```bash
curl <yourIP>
```

[OUTPUT]
```html
```

Deliverables:

- Explain the usage of each file and its contents, add comments to the different blocks if needed (we must ensure that you understood what you have done). Link to the online documentation. Link to the online documentation.

//TODO
|FileName|Explanation|
|:--|:--|
||||


* Deliver a folder "ansible" with your configuration.