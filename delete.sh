kubectl

kubectl patch mongoservices.falcon.services/mongodb -p  '{"metadata":{"finalizers":[]}}' --type=merge -n test-ns