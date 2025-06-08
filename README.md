# SMS Forwarder Android App

An Android application that automatically forwards incoming SMS messages to a specified email address using Gmail SMTP. The app runs as a background service and provides a simple toggle switch to enable/disable the forwarding functionality.

## Features

- **Automatic SMS Detection**: Listens for incoming SMS messages in real-time
- **Email Forwarding**: Sends SMS content via email using Gmail SMTP
- **Background Service**: Runs continuously in the background with a persistent notification
- **Toggle Control**: Simple on/off switch to enable/disable SMS forwarding
- **Persistent Settings**: Remembers user preferences across app restarts

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd sms-forwarder-android
```

### 2. Configure Email Settings

Create or modify the `local.properties` file in your project root and add the following environment variables:

```properties
# Gmail Configuration
SENDER_EMAIL=your-gmail@gmail.com
GMAIL_PASSWORD=your-app-password
RECIPIENT_EMAIL=recipient@example.com
```

## Email Format

Forwarded emails will have the following format:

**Subject**: `New SMS from: [Sender Phone Number]`

**Body**:
```
Sender: [Phone Number]

Message:
[SMS Content]
```
