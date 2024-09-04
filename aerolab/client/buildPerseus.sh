echo "Uploading the Perseus Setup File"
aerolab files upload -c -n ${CLIENT_NAME} $PREFIX"/../client/templates/perseus_setup.sh" /root/perseus_setup.sh || exit 1

echo "Building Perseus"
aerolab client attach -n ${CLIENT_NAME} -l all --parallel -- bash /root/perseus_setup.sh