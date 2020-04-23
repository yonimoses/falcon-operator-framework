
echo 'Installing MongoDB Resources'
kubectl apply -f vendor/crd.yaml
echo 'MongoDB Resources installed'
kubectl apply -f falcon/crd.yaml
echo 'Falcon CRD installed'
kubectl apply -f vendor/crd.yaml

