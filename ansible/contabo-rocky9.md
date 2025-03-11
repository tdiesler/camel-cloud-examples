
## Rocky 9 on Contabo VPS

https://contabo.com/en/vps

### Setup the core user

```
ssh root@vps

HOSTNAME=camel01
RSA_PUBKEY="ssh-rsa..."

NUSER=core
useradd -G root -m $NUSER -s /bin/bash
mkdir /home/$NUSER/.ssh
echo "${RSA_PUBKEY}" > /home/$NUSER/.ssh/authorized_keys
chmod 700 /home/$NUSER/.ssh
chown -R $NUSER.$NUSER /home/$NUSER/.ssh

cat << EOF > /etc/sudoers.d/user-privs-$NUSER
$NUSER ALL=(ALL:ALL) NOPASSWD: ALL
EOF

echo $HOSTNAME | sudo tee /etc/hostname
sudo hostname -b $HOSTNAME
```

### Harden SSH access (optional)

```
# ------------------------------------------------------------------------------
# SSH login to core@xxx.xxx.xxx.xxx from another terminal
# ------------------------------------------------------------------------------

# Assign a random SSH port above 10000
rnd=$((10000+$RANDOM%20000))
sudo sed -i "s/#Port 22$/Port $rnd/" /etc/ssh/sshd_config

# Disable password authentication
sudo sed -i "s/PasswordAuthentication yes$/PasswordAuthentication no/" /etc/ssh/sshd_config

# Disable challenge response authentication
sudo sed -i "s/ChallengeResponseAuthentication yes$/ChallengeResponseAuthentication no/" /etc/ssh/sshd_config

# Disable root login
sudo sed -i "s/PermitRootLogin yes$/PermitRootLogin no/" /etc/ssh/sshd_config

# Disable X11Forwarding
sudo sed -i "s/X11Forwarding yes$/X11Forwarding no/" /etc/ssh/sshd_config

sudo cat /etc/ssh/sshd_config | egrep "^Port"
sudo cat /etc/ssh/sshd_config | egrep "PasswordAuthentication"
sudo cat /etc/ssh/sshd_config | egrep "ChallengeResponseAuthentication"
sudo cat /etc/ssh/sshd_config | egrep "PermitRootLogin"
sudo cat /etc/ssh/sshd_config | egrep "X11Forwarding"

sudo systemctl restart sshd
```
