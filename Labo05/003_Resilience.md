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
  tee result_get.bin | \
  vegeta plot > plot_get.html

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

```txt
Difficulties: we did have some trouble finding the metrics of the k8s cluster and the pods. We indeed had to install the metrics server as suggester to get the CPU utilization metrics. Once installed, it took a few minutes for the metrics to be available and the autoscaling to work properly.
```

**Vegeta results**:

```
We stressed the `all` endpoint with 500 requests. Altough we tried stressing the `api/todos` endpoint, we couldn't get Vegeta to work with the POST method.

While stressing the `all` endpoint, the `kubectl get pods --watch` command indeed shows that multiple pods where created. After a cooldown period, the pods are deleted automatically and the deployement goes back to the initial replica count.

We observe an initial spike of 1200ms in the latency at the start of the stress test. After as little as a second, the other pods are running and the latency is reduced to an average of 350 ms per request.
```

![vegeta-plot-all](img/vegeta-plot-all.png)

```bash
$ kubectl describe hpa frontend-deployment
Name:                                                  frontend-deployment
Namespace:                                             default
Labels:                                                <none>
Annotations:                                           <none>
CreationTimestamp:                                     Sun, 19 May 2024 12:45:14 +0200
Reference:                                             Deployment/frontend-deployment
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  0% (0) / 30%
Min replicas:                                          1
Max replicas:                                          4
Deployment pods:                                       1 current / 1 desired
Conditions:
  Type            Status  Reason            Message
  ----            ------  ------            -------
  AbleToScale     True    ReadyForNewScale  recommended size matches current size
  ScalingActive   True    ValidMetricFound  the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  True    TooFewReplicas    the desired replica count is less than the minimum replica count
Events:
  Type     Reason                   Age                       From                       Message
  ----     ------                   ----                      ----                       -------
  Warning  FailedGetResourceMetric  9m50s (x1390 over 6h29m)  horizontal-pod-autoscaler  No recommendation
```````

```yaml
$ kubectl describe all
Name:             api-deployment-664fbdf7d9-dr86s
Namespace:        default
Priority:         0
Service Account:  default
Node:             gke-gke-cluster-1-default-pool-fee6c77f-f46f/10.138.0.4
Start Time:       Thu, 16 May 2024 15:19:37 +0200
Labels:           app=todo
                  component=api
                  pod-template-hash=664fbdf7d9
Annotations:      <none>
Status:           Running
IP:               10.36.1.11
IPs:
  IP:           10.36.1.11
Controlled By:  ReplicaSet/api-deployment-664fbdf7d9
Containers:
  api:
    Container ID:   containerd://11b47afc95c5ede0e1265f794b0b1bb7c7db195a83e522bd08d091ddd380dc38
    Image:          icclabcna/ccp2-k8s-todo-api
    Image ID:       docker.io/icclabcna/ccp2-k8s-todo-api@sha256:13cb50bc9e93fdf10b4608f04f2966e274470f00c0c9f60815ec8fc987cd6e03
    Port:           8081/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Thu, 16 May 2024 15:21:56 +0200
    Last State:     Terminated
      Reason:       Error
      Exit Code:    1
      Started:      Thu, 16 May 2024 15:19:40 +0200
      Finished:     Thu, 16 May 2024 15:21:55 +0200
    Ready:          True
    Restart Count:  1
    Environment:
      REDIS_ENDPOINT:  redis-svc
      REDIS_PWD:       ccp2
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-6qh9c (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-6qh9c:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age                From               Message
  ----    ------     ----               ----               -------
  Normal  Scheduled  42m                default-scheduler  Successfully assigned default/api-deployment-664fbdf7d9-dr86s to gke-gke-cluster-1-default-pool-fee6c77f-f46f
  Normal  Pulled     42m                kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-api" in 685ms (685ms including waiting)
  Normal  Pulling    40m (x2 over 42m)  kubelet            Pulling image "icclabcna/ccp2-k8s-todo-api"
  Normal  Created    40m (x2 over 42m)  kubelet            Created container api
  Normal  Started    40m (x2 over 42m)  kubelet            Started container api
  Normal  Pulled     40m                kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-api" in 371ms (373ms including waiting)


Name:             api-deployment-664fbdf7d9-r9nzl
Namespace:        default
Priority:         0
Service Account:  default
Node:             gke-gke-cluster-1-default-pool-fee6c77f-cqn3/10.138.0.3
Start Time:       Thu, 16 May 2024 15:19:37 +0200
Labels:           app=todo
                  component=api
                  pod-template-hash=664fbdf7d9
Annotations:      <none>
Status:           Running
IP:               10.36.0.17
IPs:
  IP:           10.36.0.17
Controlled By:  ReplicaSet/api-deployment-664fbdf7d9
Containers:
  api:
    Container ID:   containerd://275eae6f14e7068cf4e70f194115879c1560fd35591a2737ce435ab92cb4b46e
    Image:          icclabcna/ccp2-k8s-todo-api
    Image ID:       docker.io/icclabcna/ccp2-k8s-todo-api@sha256:13cb50bc9e93fdf10b4608f04f2966e274470f00c0c9f60815ec8fc987cd6e03
    Port:           8081/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Thu, 16 May 2024 15:19:54 +0200
    Last State:     Terminated
      Reason:       Error
      Exit Code:    1
      Started:      Thu, 16 May 2024 15:19:40 +0200
      Finished:     Thu, 16 May 2024 15:19:48 +0200
    Ready:          True
    Restart Count:  1
    Environment:
      REDIS_ENDPOINT:  redis-svc
      REDIS_PWD:       ccp2
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-nmb44 (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-nmb44:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age                From               Message
  ----    ------     ----               ----               -------
  Normal  Scheduled  42m                default-scheduler  Successfully assigned default/api-deployment-664fbdf7d9-r9nzl to gke-gke-cluster-1-default-pool-fee6c77f-cqn3
  Normal  Pulled     42m                kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-api" in 453ms (453ms including waiting)
  Normal  Pulling    42m (x2 over 42m)  kubelet            Pulling image "icclabcna/ccp2-k8s-todo-api"
  Normal  Pulled     42m                kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-api" in 1.036s (1.037s including waiting)
  Normal  Created    42m (x2 over 42m)  kubelet            Created container api
  Normal  Started    42m (x2 over 42m)  kubelet            Started container api


Name:             frontend-deployment-67879ff5df-7hn9n
Namespace:        default
Priority:         0
Service Account:  default
Node:             gke-gke-cluster-1-default-pool-fee6c77f-f46f/10.138.0.4
Start Time:       Thu, 16 May 2024 15:19:37 +0200
Labels:           app=todo
                  component=frontend
                  pod-template-hash=67879ff5df
Annotations:      <none>
Status:           Running
IP:               10.36.1.10
IPs:
  IP:           10.36.1.10
Controlled By:  ReplicaSet/frontend-deployment-67879ff5df
Containers:
  frontend:
    Container ID:   containerd://27cbcab09cd3af653107efb5b870de3fb1eadc62f8ab024877abc66e4ac9fc99
    Image:          icclabcna/ccp2-k8s-todo-frontend
    Image ID:       docker.io/icclabcna/ccp2-k8s-todo-frontend@sha256:5892b8f75a4dd3aa9d9cf527f8796a7638dba574ea8e6beef49360a3c67bbb44
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Thu, 16 May 2024 15:19:40 +0200
    Ready:          True
    Restart Count:  0
    Environment:
      API_ENDPOINT_URL:  http://api-svc:8081
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-grbmm (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-grbmm:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  42m   default-scheduler  Successfully assigned default/frontend-deployment-67879ff5df-7hn9n to gke-gke-cluster-1-default-pool-fee6c77f-f46f
  Normal  Pulling    42m   kubelet            Pulling image "icclabcna/ccp2-k8s-todo-frontend"
  Normal  Pulled     42m   kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-frontend" in 1.067s (1.067s including waiting)
  Normal  Created    42m   kubelet            Created container frontend
  Normal  Started    42m   kubelet            Started container frontend


Name:             frontend-deployment-67879ff5df-tdqw2
Namespace:        default
Priority:         0
Service Account:  default
Node:             gke-gke-cluster-1-default-pool-fee6c77f-cqn3/10.138.0.3
Start Time:       Thu, 16 May 2024 15:19:37 +0200
Labels:           app=todo
                  component=frontend
                  pod-template-hash=67879ff5df
Annotations:      <none>
Status:           Running
IP:               10.36.0.16
IPs:
  IP:           10.36.0.16
Controlled By:  ReplicaSet/frontend-deployment-67879ff5df
Containers:
  frontend:
    Container ID:   containerd://e1afafddfc97452dfdd56e4b11438295d95109edc6550bc665f8cfba8b2f2559
    Image:          icclabcna/ccp2-k8s-todo-frontend
    Image ID:       docker.io/icclabcna/ccp2-k8s-todo-frontend@sha256:5892b8f75a4dd3aa9d9cf527f8796a7638dba574ea8e6beef49360a3c67bbb44
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Thu, 16 May 2024 15:19:40 +0200
    Ready:          True
    Restart Count:  0
    Environment:
      API_ENDPOINT_URL:  http://api-svc:8081
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-vtblt (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-vtblt:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  42m   default-scheduler  Successfully assigned default/frontend-deployment-67879ff5df-tdqw2 to gke-gke-cluster-1-default-pool-fee6c77f-cqn3
  Normal  Pulling    42m   kubelet            Pulling image "icclabcna/ccp2-k8s-todo-frontend"
  Normal  Pulled     42m   kubelet            Successfully pulled image "icclabcna/ccp2-k8s-todo-frontend" in 468ms (468ms including waiting)
  Normal  Created    42m   kubelet            Created container frontend
  Normal  Started    42m   kubelet            Started container frontend


Name:             redis-deployment-56fb88dd96-59g92
Namespace:        default
Priority:         0
Service Account:  default
Node:             gke-gke-cluster-1-default-pool-fee6c77f-cqn3/10.138.0.3
Start Time:       Thu, 16 May 2024 15:19:37 +0200
Labels:           app=todo
                  component=redis
                  pod-template-hash=56fb88dd96
Annotations:      <none>
Status:           Running
IP:               10.36.0.18
IPs:
  IP:           10.36.0.18
Controlled By:  ReplicaSet/redis-deployment-56fb88dd96
Containers:
  redis:
    Container ID:  containerd://b3c1f92209170402a898ff0d6ca3122344fe6722e9fc8b75996b7f63b40c5544
    Image:         redis
    Image ID:      docker.io/library/redis@sha256:5a93f6b2e391b78e8bd3f9e7e1e1e06aeb5295043b4703fb88392835cec924a0
    Port:          6379/TCP
    Host Port:     0/TCP
    Args:
      redis-server
      --requirepass ccp2
      --appendonly yes
    State:          Running
      Started:      Thu, 16 May 2024 15:19:53 +0200
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-9tgbj (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-9tgbj:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  42m   default-scheduler  Successfully assigned default/redis-deployment-56fb88dd96-59g92 to gke-gke-cluster-1-default-pool-fee6c77f-cqn3
  Normal  Pulling    42m   kubelet            Pulling image "redis"
  Normal  Pulled     42m   kubelet            Successfully pulled image "redis" in 11.663s (11.666s including waiting)
  Normal  Created    42m   kubelet            Created container redis
  Normal  Started    42m   kubelet            Started container redis


Name:              api-svc
Namespace:         default
Labels:            component=api
Annotations:       cloud.google.com/neg: {"ingress":true}
Selector:          app=todo,component=api
Type:              ClusterIP
IP Family Policy:  SingleStack
IP Families:       IPv4
IP:                10.93.113.153
IPs:               10.93.113.153
Port:              api  8081/TCP
TargetPort:        8081/TCP
Endpoints:         10.36.0.17:8081,10.36.1.11:8081
Session Affinity:  None
Events:            <none>


Name:                     frontend-svc
Namespace:                default
Labels:                   component=frontend
Annotations:              cloud.google.com/neg: {"ingress":true}
Selector:                 app=todo,component=frontend
Type:                     LoadBalancer
IP Family Policy:         SingleStack
IP Families:              IPv4
IP:                       10.93.120.235
IPs:                      10.93.120.235
LoadBalancer Ingress:     34.168.190.38
Port:                     frontend  80/TCP
TargetPort:               8080/TCP
NodePort:                 frontend  31261/TCP
Endpoints:                10.36.0.16:8080,10.36.1.10:8080
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>


Name:              kubernetes
Namespace:         default
Labels:            component=apiserver
                   provider=kubernetes
Annotations:       <none>
Selector:          <none>
Type:              ClusterIP
IP Family Policy:  SingleStack
IP Families:       IPv4
IP:                10.93.112.1
IPs:               10.93.112.1
Port:              https  443/TCP
TargetPort:        443/TCP
Endpoints:         10.138.0.2:443
Session Affinity:  None
Events:            <none>


Name:              redis-svc
Namespace:         default
Labels:            component=redis
Annotations:       cloud.google.com/neg: {"ingress":true}
Selector:          app=todo,component=redis
Type:              ClusterIP
IP Family Policy:  SingleStack
IP Families:       IPv4
IP:                10.93.120.67
IPs:               10.93.120.67
Port:              redis  6379/TCP
TargetPort:        6379/TCP
Endpoints:         10.36.0.18:6379
Session Affinity:  None
Events:            <none>


Name:                   api-deployment
Namespace:              default
CreationTimestamp:      Mon, 13 May 2024 15:14:55 +0200
Labels:                 app=todo
                        component=api
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=todo,component=api
Replicas:               2 desired | 2 updated | 2 total | 2 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=api
  Containers:
   api:
    Image:      icclabcna/ccp2-k8s-todo-api
    Port:       8081/TCP
    Host Port:  0/TCP
    Environment:
      REDIS_ENDPOINT:  redis-svc
      REDIS_PWD:       ccp2
    Mounts:            <none>
  Volumes:             <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Progressing    True    NewReplicaSetAvailable
  Available      True    MinimumReplicasAvailable
OldReplicaSets:  <none>
NewReplicaSet:   api-deployment-664fbdf7d9 (2/2 replicas created)
Events:          <none>


Name:                   frontend-deployment
Namespace:              default
CreationTimestamp:      Mon, 13 May 2024 15:15:15 +0200
Labels:                 app=todo
                        component=frontend
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=todo,component=frontend
Replicas:               2 desired | 2 updated | 2 total | 2 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=frontend
  Containers:
   frontend:
    Image:      icclabcna/ccp2-k8s-todo-frontend
    Port:       8080/TCP
    Host Port:  0/TCP
    Environment:
      API_ENDPOINT_URL:  http://api-svc:8081
    Mounts:              <none>
  Volumes:               <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Progressing    True    NewReplicaSetAvailable
  Available      True    MinimumReplicasAvailable
OldReplicaSets:  <none>
NewReplicaSet:   frontend-deployment-67879ff5df (2/2 replicas created)
Events:          <none>


Name:                   redis-deployment
Namespace:              default
CreationTimestamp:      Mon, 13 May 2024 15:14:33 +0200
Labels:                 app=todo
                        component=redis
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=todo,component=redis
Replicas:               1 desired | 1 updated | 1 total | 1 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=redis
  Containers:
   redis:
    Image:      redis
    Port:       6379/TCP
    Host Port:  0/TCP
    Args:
      redis-server
      --requirepass ccp2
      --appendonly yes
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Progressing    True    NewReplicaSetAvailable
  Available      True    MinimumReplicasAvailable
OldReplicaSets:  <none>
NewReplicaSet:   redis-deployment-56fb88dd96 (1/1 replicas created)
Events:          <none>


Name:           api-deployment-664fbdf7d9
Namespace:      default
Selector:       app=todo,component=api,pod-template-hash=664fbdf7d9
Labels:         app=todo
                component=api
                pod-template-hash=664fbdf7d9
Annotations:    deployment.kubernetes.io/desired-replicas: 2
                deployment.kubernetes.io/max-replicas: 3
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/api-deployment
Replicas:       2 current / 2 desired
Pods Status:    2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=todo
           component=api
           pod-template-hash=664fbdf7d9
  Containers:
   api:
    Image:      icclabcna/ccp2-k8s-todo-api
    Port:       8081/TCP
    Host Port:  0/TCP
    Environment:
      REDIS_ENDPOINT:  redis-svc
      REDIS_PWD:       ccp2
    Mounts:            <none>
  Volumes:             <none>
Events:
  Type    Reason            Age   From                   Message
  ----    ------            ----  ----                   -------
  Normal  SuccessfulCreate  42m   replicaset-controller  Created pod: api-deployment-664fbdf7d9-r9nzl
  Normal  SuccessfulCreate  42m   replicaset-controller  Created pod: api-deployment-664fbdf7d9-dr86s


Name:           frontend-deployment-67879ff5df
Namespace:      default
Selector:       app=todo,component=frontend,pod-template-hash=67879ff5df
Labels:         app=todo
                component=frontend
                pod-template-hash=67879ff5df
Annotations:    deployment.kubernetes.io/desired-replicas: 2
                deployment.kubernetes.io/max-replicas: 3
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/frontend-deployment
Replicas:       2 current / 2 desired
Pods Status:    2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=todo
           component=frontend
           pod-template-hash=67879ff5df
  Containers:
   frontend:
    Image:      icclabcna/ccp2-k8s-todo-frontend
    Port:       8080/TCP
    Host Port:  0/TCP
    Environment:
      API_ENDPOINT_URL:  http://api-svc:8081
    Mounts:              <none>
  Volumes:               <none>
Events:
  Type    Reason            Age   From                   Message
  ----    ------            ----  ----                   -------
  Normal  SuccessfulCreate  42m   replicaset-controller  Created pod: frontend-deployment-67879ff5df-tdqw2
  Normal  SuccessfulCreate  42m   replicaset-controller  Created pod: frontend-deployment-67879ff5df-7hn9n


Name:           redis-deployment-56fb88dd96
Namespace:      default
Selector:       app=todo,component=redis,pod-template-hash=56fb88dd96
Labels:         app=todo
                component=redis
                pod-template-hash=56fb88dd96
Annotations:    deployment.kubernetes.io/desired-replicas: 1
                deployment.kubernetes.io/max-replicas: 2
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/redis-deployment
Replicas:       1 current / 1 desired
Pods Status:    1 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=todo
           component=redis
           pod-template-hash=56fb88dd96
  Containers:
   redis:
    Image:      redis
    Port:       6379/TCP
    Host Port:  0/TCP
    Args:
      redis-server
      --requirepass ccp2
      --appendonly yes
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Events:
  Type    Reason            Age   From                   Message
  ----    ------            ----  ----                   -------
  Normal  SuccessfulCreate  42m   replicaset-controller  Created pod: redis-deployment-56fb88dd96-59g92
```

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
        resources:
          requests:
            cpu: 10m
```
