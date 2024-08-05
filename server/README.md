# Server for TenderUs

## How to download and run

To clone and run this application, you'll need Git and Node.js (which comes with npm).

### 1. Clone this repository

```sh
git clone https://github.com/NationalWind/TenderUS.git
```

### 2. Go to the Server folder

```sh
cd ./server
```

### 3. Add .env file.
Configure the file following the same format as .env.example.

### 4. Go to your firebase console
1. 'Generate new private key' from Project settings/Service accounts.

2. A json file is then downloaded.

3. Rename it as download-from-firebase-project-settings-service-accounts.json and move it to the server folder.

### 5. HTTPS for dev -> Generate a simple self-signed ssl certificate.

Create a 'cert' folder then add three files cert.pem, csr.pem, key.pem generated from your self-signing using openssl or sth.

For example:
```sh
openssl genpkey -algorithm RSA -out key.pem -pkeyopt rsa_keygen_bits:2048
openssl req -new -key key.pem -out csr.pem
openssl req -x509 -key key.pem -in csr.pem -out cert.pem -days 365
```
~~You might enter to skip prompts from the commands.~~


### 6. Install dependencies

```sh
npm install
```

### 7. Start the server

```sh
npm run dev
```

Now, the server is running at `http://localhost:YOUR_HTTP_PORT` and `https://localhost:YOUR_HTTPS_PORT`
