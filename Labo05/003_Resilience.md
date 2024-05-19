# Task 3 - Add and exercise resilience

By now you should have understood the general principle of configuring, running and accessing applications in Kubernetes. However, the above application has no support for resilience. If a container (resp. Pod) dies, it stops working. Next, we add some resilience to the application.

## Subtask 3.1 - Add Deployments

In this task you will create Deployments that will spawn Replica Sets as health-management components.

Converting a Pod to be manag  * Create Deployment versions of your application configurations (e.g. `redis-deploy.yaml` instead of `redis-pod.yaml`) and modify/extend them to contain the required Deployment parameters.
ed by a Deployment is quite simple.

  * Have a look at an example of a Deployment described here: <https://kubernetes.io/docs/concepts/workloads/controllers/deployment/>

  * Create Deployment versions of your application configurations (e.g. `redis-deploy.yaml` instead of `redis-pod.yaml`) and modify/extend them to contain the required Deployment parameters.

  * Again, be careful with the YAML indentation!

  * Make sure to have always 2 instances of the API and Frontend running. 

  * Use only 1 instance for the Redis-Server. Why?

    ```txt
    Having a single database is easier to manage because we don't have to set up clustering and worry about data consistency between instances.
    
    A common problem with multiple instances of a database is that they can get out of sync, which can lead to data corruption.
    ```

  * Delete all application Pods (using `kubectl delete pod ...`) and replace them with deployment versions.

    ```sh
    kubectl delete pods --all
    kubectl create -f redis-deploy.yaml -f api-deploy.yaml -f frontend-deploy.yaml
    ```

  * Verify that the application is still working and the Replica Sets are in place. (`kubectl get all`, `kubectl get pods`, `kubectl describe ...`)

    ```sh
    kubectl get all
    ```

    ```txt
    NAME                                       READY   STATUS    RESTARTS       AGE
    pod/api-deployment-664fbdf7d9-l8v75        1/1     Running   2 (5m8s ago)   5m17s
    pod/api-deployment-664fbdf7d9-v7csc        1/1     Running   1 (5m9s ago)   5m17s
    pod/frontend-deployment-67879ff5df-ckg9p   1/1     Running   0              5m17s
    pod/frontend-deployment-67879ff5df-rxd78   1/1     Running   0              5m17s
    pod/redis-deployment-56fb88dd96-sfcxp      1/1     Running   0              5m18s

    NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP      PORT(S)        AGE
    service/api-svc        ClusterIP      10.93.118.108   <none>           8081/TCP       45m
    service/frontend-svc   LoadBalancer   10.93.118.40    35.203.183.244   80:31606/TCP   45m
    service/kubernetes     ClusterIP      10.93.112.1     <none>           443/TCP        56m
    service/redis-svc      ClusterIP      10.93.125.179   <none>           6379/TCP       45m

    NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
    deployment.apps/api-deployment        2/2     2            2           5m19s
    deployment.apps/frontend-deployment   2/2     2            2           5m18s
    deployment.apps/redis-deployment      1/1     1            1           5m19s

    NAME                                             DESIRED   CURRENT   READY   AGE
    replicaset.apps/api-deployment-664fbdf7d9        2         2         2       5m19s
    replicaset.apps/frontend-deployment-67879ff5df   2         2         2       5m19s
    replicaset.apps/redis-deployment-56fb88dd96      1         1         1       5m20s
    ```
    

## Subtask 3.2 - Verify the functionality of the Replica Sets

In this subtask you will intentionally kill (delete) Pods and verify that the application keeps working and the Replica Set is doing its task.

Hint: You can monitor the status of a resource by adding the `--watch` option to the `get` command. To watch a single resource:

```sh
$ kubectl get <resource-name> --watch
```

To watch all resources of a certain type, for example all Pods:

```sh
$ kubectl get pods --watch
```

You may also use `kubectl get all` repeatedly to see a list of all resources.  You should also verify if the application stays available by continuously reloading your browser window.

* What happens if you delete a Frontend or API Pod? How long does it take for the system to react?

  ```sh
  $ kubectl delete pod [pod name (for ex : api-deployment-664fbdf7d9-drjh5]
  ```

  ```txt
  When deleting a pod, the deployment immediately creates a new one to replace it in order to maintain the desired number of replicas.

  It takes about 3 seconds for a new container to be created and go through the `Pending -> Running` phases. During that time, the frontend and the API behaves just like expected.

  When both frontend pods are deleted, the app becomes unavailable for a few seconds until the new pods are running and operations can resume normally.
  ```

* What happens when you delete the Redis Pod?

  ```
  Any past data in the DB is lost because there is only a single instance of redis running (the data is not replicated in another instance) and no persistent volume is set up.
  ```
  
* How can you change the number of instances temporarily to 3? Hint: look for scaling in the deployment documentation

  ```sh
  $ kubectl scale --replicas=3 deployment/api-deployment
  ```
  
* What autoscaling features are available? Which metrics are used?

  ```txt
  There are 3 common autoscaling options:
  - Horizontal Pod Autoscaler (HPA): automatically scales the number of pods in a replication controller, deployment, replica set, or stateful set based on observed CPU utilization. By default, it scales based on the average CPU utilization of all pods and queries the metrics server every 15 seconds. Note that a minimal and maximal number of replicas may be set.
  - Vertical Pod Autoscaler (VPA): automatically adjusts the CPU and memory reservations of the pods (this doesn't come wiht k8s by default, it must be installed). By default, it is also based on the average CPU utilization of all pods.
  - Cluster Autoscaler: This feature automatically adjusts the size of the Kubernetes cluster when there are failed pods in the cluster due to insufficient resources or some nodes in the cluster are underutilized for an extended period of time and it is possible to regroup pods in less nodes.

  The metrics used are divided in three categories:
  - Resource metrics: CPU and memory usage of the pods.
  - Custom metrics: app-specific metrics defined based on the behavior of the application.
  - External metrics: metrics from an external system that are unrelated to the k8s objects but on which the cluster depends. 
  ```
  
* How can you update a component? (see "Updating a Deployment" in the deployment documentation)

  ```sh
  $ kubectl edit deployment/api-deployment --save-config # Opens the default editor with the configuration file. Saving will run kubectl --apply on the object.
  ```

## Subtask 3.3 - Put autoscaling in place and load-test it

On the GKE cluster deploy autoscaling on the Frontend with a target CPU utilization of 30% and number of replicas between 1 and 4. 

```sh
$ kubectl autoscale deployment/frontend-deployment --min=1 --max=4 --cpu-percent=30
```

Load-test using Vegeta (500 requests should be enough).

```bash
OUT="./plots"
URL="http://35.203.183.244"
DURATION="10s" # Default vegeta rate is 50/second so 500 requests will take 10 seconds

mkdir -p $OUT
pushd $OUT

# Test the `all` endpoint
echo "GET $URL/all" | \
  vegeta attack -duration=$DURATION | \
  tee result_all.bin | \
  vegeta plot > plot_all.html

# Test the /api/todos endpoint
PARAMS='{"isComplete":false,"label":"Ducky"}' 

jq -ncM "while(true; .+1) | {method: 'POST', url: '${URL}/api/todos', body: ${PARAMS} | @base64 }" | \
  vegeta attack -duration=$DURATION | \
  tee result_todos.bin | \
  vegeta plot > plot_todos.html

popd
```

> [!NOTE]
>
> - The autoscale may take a while to trigger.
>
> - If your autoscaling fails to get the cpu utilization metrics, run the following command
>
>   - ```sh
>     $ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
>     ```
>
>   - Then add the *resources* part in the *container part* in your `frontend-deploy` :
>
>   - ```yaml
>     spec:
>       containers:
>         - ...:
>           env:
>             - ...:
>           resources:
>             requests:
>               cpu: 10m
>     ```
>

## Deliverables

Document your observations in the lab report. Document any difficulties you faced and how you overcame them. Copy the object descriptions into the lab report.

> // TODO

```````sh
// TODO object descriptions
// TODO autoscaling describe
```````

```yaml
# redis-deploy.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-deployment
  labels:
    component: redis
    app: todo
spec:
  replicas: 1
  selector:
    matchLabels:
      component: redis
      app: todo
  template:
    metadata:
      labels:
        component: redis
        app: todo
    spec:
      containers:
      - name: redis
        image: redis
        ports:
        - containerPort: 6379
        args:
        - redis-server 
        - --requirepass ccp2 
        - --appendonly yes
```

```yaml
# api-deploy.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-deployment
  labels:
    component: api
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: api
      app: todo
  template:
    metadata:
      labels:
        component: api
        app: todo
    spec:
      containers:
      - name: api
        image: icclabcna/ccp2-k8s-todo-api
        ports:
        - containerPort: 8081
        env:
        - name: REDIS_ENDPOINT
          value: redis-svc
        - name: REDIS_PWD
          value: ccp2

```

```yaml
# frontend-deploy.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-deployment
  labels:
    component: frontend
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: frontend
      app: todo
  template:
    metadata:
      labels:
        component: frontend
        app: todo
    spec:
      containers:
      - name: frontend
        image: icclabcna/ccp2-k8s-todo-frontend
        ports:
        - containerPort: 8080
        env:
        - name: API_ENDPOINT_URL
          value: http://api-svc:8081
```
