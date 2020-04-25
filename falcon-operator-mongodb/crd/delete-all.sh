#kubectl patch ns/falcon -p '{"metadata":{"finalizers":[]}}' --type=merge
#kubectl patch ns/mongo -p '{"metadata":{"finalizers":[]}}' --type=merge
echo 'Deleting MongoDBUser'
kubectl patch  MongoDBUser mongo-user-test -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete MongoDBUser mongo-user-test  -n mongo

kubectl patch  MongoService mongo -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete MongoService mongo -n falcon

echo 'Patching mongodbusers.mongodb.com'

kubectl patch crd/mongodbusers.mongodb.com -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete crd/mongodbusers.mongodb.com

echo 'Patching mongoservices.falcon.services'
kubectl patch crd/mongoservices.falcon.services -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete crd/mongoservices.falcon.services

echo 'Deleting Falcon'
kubectl patch ns/falcon -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete ns falcon

echo 'Deleting Mongo'
kubectl patch ns/mongo -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl delete ns mongo
