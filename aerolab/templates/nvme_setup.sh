blkdiscard /dev/nvme1n1
parted -s /dev/nvme1n1 "mktable gpt"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 0% 7%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 7% 14%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 14% 21%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 21% 28%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 28% 35%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 35% 42%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 42% 49%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 49% 56%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 56% 63%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 63% 70%"
parted -a optimal -s /dev/nvme1n1 "mkpart primary 70% 77%"
sleep 10
blkdiscard -z --length 8MiB /dev/nvme1n1p1
blkdiscard -z --length 8MiB /dev/nvme1n1p2
blkdiscard -z --length 8MiB /dev/nvme1n1p3
blkdiscard -z --length 8MiB /dev/nvme1n1p4
blkdiscard -z --length 8MiB /dev/nvme1n1p5
blkdiscard -z --length 8MiB /dev/nvme1n1p6
blkdiscard -z --length 8MiB /dev/nvme1n1p7
blkdiscard -z --length 8MiB /dev/nvme1n1p8
blkdiscard -z --length 8MiB /dev/nvme1n1p9
blkdiscard -z --length 8MiB /dev/nvme1n1p10
blkdiscard -z --length 8MiB /dev/nvme1n1p11