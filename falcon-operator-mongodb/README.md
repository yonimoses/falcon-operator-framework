PATCH DELET 
kubectl patch mongoservices.falcon.services/mongodb -n test-ns -p '{"metadata":{"finalizers":null}}'