mvn clean package -DskipTests

#docker rmi -f dispatcher-operator:$1
docker build -t yonimoses/falcon-mongodb:$1 .
docker login
docker push yonimoses/falcon-mongodb:$1
docker rmi yonimoses/falcon-mongodb:$1
# sed "s/_VER_/"$1"/g" kubernetes.yaml.tmpl > kubernetes.yaml
sed "s/_VER_/$1/" kubernetes.yaml.tmpl > kubernetes.yaml
kubectl delete ns bnhp-falcon-operator-ns
echo 'Sleeping for 10 seconds'
sleep 10
kubectl apply -f kubernetes.yaml
echo 'Go get the CRD'
