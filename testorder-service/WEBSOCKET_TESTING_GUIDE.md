# WebSocket Testing Guide - TestOrder Service

## Overview

Hướng dẫn này giúp bạn test WebSocket notification system của TestOrder service để đảm bảo:
- WebSocket connection hoạt động đúng
- Notifications được push real-time
- Privilege-based filtering hoạt động
- Cross-origin requests được hỗ trợ

## Prerequisites

1. TestOrder service đang chạy trên `localhost:8082`
2. MongoDB đang chạy để lưu notifications
3. RabbitMQ đang chạy để xử lý message queues
4. IAM service đang chạy để authentication
5. **Có JWT token hợp lệ để authentication** (xem section Authentication bên dưới)

## Authentication Setup

### Lấy JWT Token

Trước khi test WebSocket và REST APIs, bạn cần lấy JWT token hợp lệ từ IAM service:

#### Method 1: Qua IAM Service Login API

```bash
# Login để lấy JWT token
curl -X POST http://localhost:8080/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your-username",
    "password": "your-password"
  }'

# Response sẽ chứa access token:
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

#### Method 2: Qua Frontend Application

1. Login vào frontend application
2. Mở Developer Tools (F12)
3. Vào tab Application/Storage -> Local Storage
4. Tìm key chứa JWT token (thường là `token`, `access_token`, hoặc `jwt`)
5. Copy token value

#### Method 3: Tạo Test User

```bash
# Tạo test user nếu chưa có
curl -X POST http://localhost:8080/iam/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "email": "test@example.com",
    "fullName": "Test User"
  }'

# Sau đó login với user vừa tạo
curl -X POST http://localhost:8080/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }'
```

### Verify Token

```bash
# Test token có hợp lệ không
JWT_TOKEN="your-jwt-token-here"

curl -H "Authorization: Bearer $JWT_TOKEN" \
     http://localhost:8082/testorder/notifications/health

# Nếu response là "Notifications service is healthy" thì token hợp lệ
```

### Token Expiry

⚠️ **Lưu ý**: JWT tokens có thời gian hết hạn (thường 1-24 giờ). Nếu test fails với 401 Unauthorized, hãy lấy token mới.

```bash
# Check token expiry (nếu có jq installed)
echo "your-jwt-token-here" | cut -d'.' -f2 | base64 -d | jq '.exp'

# Hoặc sử dụng online JWT decoder: https://jwt.io
```

## Method 1: Browser JavaScript Testing

### 1.1 HTML Test Page

Tạo file `websocket-test.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>TestOrder WebSocket Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>
</head>
<body>
    <h1>TestOrder WebSocket Test</h1>
    
    <div>
        <h3>JWT Token Setup:</h3>
        <input type="text" id="jwtToken" placeholder="Paste your JWT token here" style="width: 500px;">
        <button onclick="setToken()">Set Token</button>
        <div id="tokenStatus" style="margin-top: 5px; font-size: 12px;"></div>
    </div>

    <div style="margin-top: 15px;">
        <h3>Connection Status: <span id="status">Disconnected</span></h3>
        <button onclick="connect()">Connect</button>
        <button onclick="disconnect()">Disconnect</button>
    </div>

    <div style="margin-top: 20px;">
        <h3>Subscriptions:</h3>
        <button onclick="subscribeToAll()">Subscribe to All Notifications</button>
        <button onclick="subscribeToCreated()">Subscribe to Created</button>
        <button onclick="subscribeToUpdated()">Subscribe to Updated</button>
        <button onclick="subscribeToUrgent()">Subscribe to Urgent</button>
    </div>

    <div style="margin-top: 20px;">
        <h3>API Testing:</h3>
        <button onclick="testCreateOrder()">Test Create Order</button>
        <button onclick="testUpdateOrder()">Test Update Order</button>
        <button onclick="getNotifications()">Get Notifications</button>
        <button onclick="getUnreadCount()">Get Unread Count</button>
    </div>

    <div style="margin-top: 20px;">
        <h3>Received Messages:</h3>
        <div id="messages" style="border: 1px solid #ccc; height: 300px; overflow-y: scroll; padding: 10px;"></div>
        <button onclick="clearMessages()">Clear Messages</button>
    </div>

    <script>
        let stompClient = null;
        let subscriptions = [];
        let jwtToken = null;
        let lastCreatedOrderId = null;

        function connect() {
            const socket = new SockJS('http://localhost:8082/testorder/ws');
            stompClient = new StompJs.Client({
                webSocketFactory: () => socket,
                debug: function (str) {
                    console.log('STOMP: ' + str);
                },
                onConnect: function (frame) {
                    console.log('Connected: ' + frame);
                    document.getElementById('status').textContent = 'Connected';
                    document.getElementById('status').style.color = 'green';
                },
                onStompError: function (frame) {
                    console.error('Broker reported error: ' + frame.headers['message']);
                    console.error('Additional details: ' + frame.body);
                    document.getElementById('status').textContent = 'Error';
                    document.getElementById('status').style.color = 'red';
                },
                onDisconnect: function () {
                    console.log('Disconnected');
                    document.getElementById('status').textContent = 'Disconnected';
                    document.getElementById('status').style.color = 'red';
                }
            });

            stompClient.activate();
        }

        function disconnect() {
            if (stompClient !== null) {
                stompClient.deactivate();
            }
        }

        function subscribeToAll() {
            if (stompClient && stompClient.connected) {
                const subscription = stompClient.subscribe('/topic/testorder', function (notification) {
                    const event = JSON.parse(notification.body);
                    displayMessage('ALL', event);
                });
                subscriptions.push(subscription);
                console.log('Subscribed to /topic/testorder');
            }
        }

        function subscribeToCreated() {
            if (stompClient && stompClient.connected) {
                const subscription = stompClient.subscribe('/topic/testorder/created', function (notification) {
                    const event = JSON.parse(notification.body);
                    displayMessage('CREATED', event);
                });
                subscriptions.push(subscription);
                console.log('Subscribed to /topic/testorder/created');
            }
        }

        function subscribeToUpdated() {
            if (stompClient && stompClient.connected) {
                const subscription = stompClient.subscribe('/topic/testorder/updated', function (notification) {
                    const event = JSON.parse(notification.body);
                    displayMessage('UPDATED', event);
                });
                subscriptions.push(subscription);
                console.log('Subscribed to /topic/testorder/updated');
            }
        }

        function subscribeToUrgent() {
            if (stompClient && stompClient.connected) {
                const subscription = stompClient.subscribe('/topic/testorder/urgent', function (notification) {
                    const event = JSON.parse(notification.body);
                    displayMessage('URGENT', event);
                });
                subscriptions.push(subscription);
                console.log('Subscribed to /topic/testorder/urgent');
            }
        }

        function displayMessage(topic, event) {
            const messagesDiv = document.getElementById('messages');
            const messageElement = document.createElement('div');
            messageElement.style.marginBottom = '10px';
            messageElement.style.padding = '5px';
            messageElement.style.border = '1px solid #ddd';
            messageElement.style.backgroundColor = topic === 'URGENT' ? '#ffebee' : '#f5f5f5';
            
            const timestamp = new Date().toLocaleTimeString();
            messageElement.innerHTML = `
                <strong>[${timestamp}] ${topic}:</strong><br>
                <strong>Title:</strong> ${event.title}<br>
                <strong>Message:</strong> ${event.message}<br>
                <strong>Action:</strong> ${event.action}<br>
                <strong>Entity ID:</strong> ${event.entityId}<br>
                <strong>Event ID:</strong> ${event.eventId}<br>
                <details>
                    <summary>Full Event Data</summary>
                    <pre>${JSON.stringify(event, null, 2)}</pre>
                </details>
            `;
            
            messagesDiv.appendChild(messageElement);
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
        }

        function clearMessages() {
            document.getElementById('messages').innerHTML = '';
        }

        // JWT Token functions
        function setToken() {
            const tokenInput = document.getElementById('jwtToken');
            jwtToken = tokenInput.value.trim();
            
            if (jwtToken) {
                // Test token validity
                fetch('http://localhost:8082/testorder/notifications/health', {
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`
                    }
                })
                .then(response => {
                    if (response.ok) {
                        document.getElementById('tokenStatus').innerHTML = 
                            '<span style="color: green;">✅ Token is valid</span>';
                    } else {
                        document.getElementById('tokenStatus').innerHTML = 
                            '<span style="color: red;">❌ Token is invalid</span>';
                    }
                })
                .catch(error => {
                    document.getElementById('tokenStatus').innerHTML = 
                        '<span style="color: red;">❌ Failed to validate token</span>';
                });
            }
        }

        // API Testing functions
        async function testCreateOrder() {
            if (!jwtToken) {
                alert('Please set JWT token first!');
                return;
            }

            const testData = {
                fullName: 'WebSocket Test User',
                email: 'websocket.test@example.com',
                address: '123 WebSocket Test St',
                phoneNumber: '0123456789',
                dateOfBirth: '1990-01-01',
                gender: 'MALE'
            };

            try {
                const response = await fetch('http://localhost:8082/testorder/testorders', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwtToken}`
                    },
                    body: JSON.stringify(testData)
                });

                if (response.ok) {
                    const result = await response.json();
                    lastCreatedOrderId = result.testId;
                    displayMessage('API', {
                        title: 'API Success',
                        message: `Test Order created with ID: ${result.testId}`,
                        action: 'CREATE_API',
                        entityId: result.testId,
                        eventId: 'manual-api-test'
                    });
                } else {
                    displayMessage('API_ERROR', {
                        title: 'API Error',
                        message: `Failed to create order: ${response.status} ${response.statusText}`,
                        action: 'ERROR',
                        entityId: 'N/A',
                        eventId: 'error'
                    });
                }
            } catch (error) {
                displayMessage('API_ERROR', {
                    title: 'API Error',
                    message: `Network error: ${error.message}`,
                    action: 'ERROR',
                    entityId: 'N/A',
                    eventId: 'error'
                });
            }
        }

        async function testUpdateOrder() {
            if (!jwtToken) {
                alert('Please set JWT token first!');
                return;
            }

            if (!lastCreatedOrderId) {
                alert('Please create an order first!');
                return;
            }

            const updateData = {
                fullName: 'WebSocket Test User Updated',
                address: '456 Updated WebSocket St',
                phone: '0987654321',
                dateOfBirth: '1990-01-01',
                gender: 'MALE'
            };

            try {
                const response = await fetch(`http://localhost:8082/testorder/testorders/${lastCreatedOrderId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwtToken}`
                    },
                    body: JSON.stringify(updateData)
                });

                if (response.ok) {
                    const result = await response.json();
                    displayMessage('API', {
                        title: 'API Success',
                        message: `Test Order ${lastCreatedOrderId} updated successfully`,
                        action: 'UPDATE_API',
                        entityId: lastCreatedOrderId,
                        eventId: 'manual-api-test'
                    });
                } else {
                    displayMessage('API_ERROR', {
                        title: 'API Error',
                        message: `Failed to update order: ${response.status} ${response.statusText}`,
                        action: 'ERROR',
                        entityId: lastCreatedOrderId,
                        eventId: 'error'
                    });
                }
            } catch (error) {
                displayMessage('API_ERROR', {
                    title: 'API Error',
                    message: `Network error: ${error.message}`,
                    action: 'ERROR',
                    entityId: lastCreatedOrderId,
                    eventId: 'error'
                });
            }
        }

        async function getNotifications() {
            if (!jwtToken) {
                alert('Please set JWT token first!');
                return;
            }

            try {
                const response = await fetch('http://localhost:8082/testorder/notifications', {
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`
                    }
                });

                if (response.ok) {
                    const notifications = await response.json();
                    displayMessage('API', {
                        title: 'Notifications Retrieved',
                        message: `Found ${notifications.length} notifications`,
                        action: 'GET_NOTIFICATIONS',
                        entityId: 'N/A',
                        eventId: 'api-get',
                        data: notifications.slice(0, 3) // Show first 3
                    });
                } else {
                    displayMessage('API_ERROR', {
                        title: 'API Error',
                        message: `Failed to get notifications: ${response.status}`,
                        action: 'ERROR',
                        entityId: 'N/A',
                        eventId: 'error'
                    });
                }
            } catch (error) {
                displayMessage('API_ERROR', {
                    title: 'API Error',
                    message: `Network error: ${error.message}`,
                    action: 'ERROR',
                    entityId: 'N/A',
                    eventId: 'error'
                });
            }
        }

        async function getUnreadCount() {
            if (!jwtToken) {
                alert('Please set JWT token first!');
                return;
            }

            try {
                const response = await fetch('http://localhost:8082/testorder/notifications/unread-count', {
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`
                    }
                });

                if (response.ok) {
                    const count = await response.text();
                    displayMessage('API', {
                        title: 'Unread Count',
                        message: `You have ${count} unread notifications`,
                        action: 'GET_UNREAD_COUNT',
                        entityId: 'N/A',
                        eventId: 'api-count'
                    });
                } else {
                    displayMessage('API_ERROR', {
                        title: 'API Error',
                        message: `Failed to get unread count: ${response.status}`,
                        action: 'ERROR',
                        entityId: 'N/A',
                        eventId: 'error'
                    });
                }
            } catch (error) {
                displayMessage('API_ERROR', {
                    title: 'API Error',
                    message: `Network error: ${error.message}`,
                    action: 'ERROR',
                    entityId: 'N/A',
                    eventId: 'error'
                });
            }
        }

        // Auto-connect when page loads
        window.onload = function() {
            connect();
        };

        // Cleanup on page unload
        window.onbeforeunload = function() {
            disconnect();
        };
    </script>
</body>
</html>
```

### 1.2 Cách sử dụng HTML Test Page

1. **Lấy JWT Token**: 
   - Login vào IAM service để lấy JWT token (xem Authentication Setup section)
   - Copy token từ response hoặc browser storage

2. **Setup Test Page**:
   - Lưu file `websocket-test.html` 
   - Mở file trong browser
   - Paste JWT token vào input field và click "Set Token"
   - Verify token status shows "✅ Token is valid"

3. **Test WebSocket**:
   - Click "Connect" để kết nối WebSocket
   - Click các nút Subscribe để đăng ký nhận notifications

4. **Test End-to-End**:
   - Click "Test Create Order" để tạo test order và trigger notification
   - Click "Test Update Order" để update order và trigger notification  
   - Click "Get Notifications" để verify notifications được lưu trong database
   - Click "Get Unread Count" để check số notifications chưa đọc

5. **Observe Results**:
   - Watch real-time WebSocket messages trong "Received Messages" section
   - Verify API responses hiển thị trong message area
   - Check both WebSocket push và REST API responses

## Method 2: Node.js Testing Script

### 2.1 Setup Node.js Environment

```bash
# Tạo project mới
mkdir websocket-test
cd websocket-test
npm init -y

# Install dependencies
npm install sockjs-client @stomp/stompjs ws
```

### 2.2 Node.js Test Script

Tạo file `test-websocket.js`:

```javascript
const SockJS = require('sockjs-client');
const { Client } = require('@stomp/stompjs');

// Polyfill cho WebSocket trong Node.js
Object.assign(global, { WebSocket: require('ws') });

class TestOrderWebSocketTester {
    constructor() {
        this.client = null;
        this.connected = false;
        this.subscriptions = [];
        this.messageCount = 0;
    }

    connect() {
        return new Promise((resolve, reject) => {
            console.log('🔌 Connecting to TestOrder WebSocket...');
            
            const socket = new SockJS('http://localhost:8082/testorder/ws');
            
            this.client = new Client({
                webSocketFactory: () => socket,
                debug: (str) => {
                    console.log(`🐛 STOMP: ${str}`);
                },
                onConnect: (frame) => {
                    console.log('✅ Connected to WebSocket');
                    console.log(`📋 Frame: ${frame}`);
                    this.connected = true;
                    resolve();
                },
                onStompError: (frame) => {
                    console.error(`❌ STOMP Error: ${frame.headers['message']}`);
                    console.error(`📋 Details: ${frame.body}`);
                    reject(new Error(frame.headers['message']));
                },
                onDisconnect: () => {
                    console.log('🔌 Disconnected from WebSocket');
                    this.connected = false;
                }
            });

            this.client.activate();
        });
    }

    subscribeToTopic(topic, label) {
        if (!this.connected) {
            console.error('❌ Not connected to WebSocket');
            return;
        }

        console.log(`📡 Subscribing to ${topic} (${label})`);
        
        const subscription = this.client.subscribe(topic, (message) => {
            this.messageCount++;
            const notification = JSON.parse(message.body);
            
            console.log(`\n📨 [${label}] Message #${this.messageCount} received:`);
            console.log(`🕒 Time: ${new Date().toISOString()}`);
            console.log(`📝 Title: ${notification.title}`);
            console.log(`💬 Message: ${notification.message}`);
            console.log(`🎯 Action: ${notification.action}`);
            console.log(`🆔 Entity ID: ${notification.entityId}`);
            console.log(`🏷️  Event ID: ${notification.eventId}`);
            console.log(`👥 Target Privileges: ${JSON.stringify(notification.targetPrivileges)}`);
            console.log(`🌐 Is Global: ${notification.isGlobal}`);
            console.log(`📊 Order Status: ${notification.orderStatus}`);
            console.log(`⚡ Priority: ${notification.priority}`);
            console.log('─'.repeat(50));
        });

        this.subscriptions.push({ subscription, topic, label });
        console.log(`✅ Subscribed to ${topic}`);
    }

    subscribeToAllTopics() {
        const topics = [
            { topic: '/topic/testorder', label: 'ALL' },
            { topic: '/topic/testorder/created', label: 'CREATED' },
            { topic: '/topic/testorder/updated', label: 'UPDATED' },
            { topic: '/topic/testorder/status', label: 'STATUS' },
            { topic: '/topic/testorder/results', label: 'RESULTS' },
            { topic: '/topic/testorder/urgent', label: 'URGENT' }
        ];

        topics.forEach(({ topic, label }) => {
            this.subscribeToTopic(topic, label);
        });
    }

    disconnect() {
        if (this.client) {
            console.log('🔌 Disconnecting...');
            this.client.deactivate();
        }
    }

    async runTest(duration = 60000) {
        try {
            await this.connect();
            this.subscribeToAllTopics();
            
            console.log(`\n🕰️  Listening for notifications for ${duration/1000} seconds...`);
            console.log('💡 Now create/update test orders to see notifications!');
            console.log('🛠️  Use REST API endpoints to trigger notifications');
            
            await new Promise(resolve => setTimeout(resolve, duration));
            
            console.log(`\n📊 Test completed. Received ${this.messageCount} messages.`);
            this.disconnect();
            
        } catch (error) {
            console.error(`❌ Test failed: ${error.message}`);
            this.disconnect();
        }
    }
}

// Run the test
// Configuration - REPLACE WITH YOUR ACTUAL JWT TOKEN
const JWT_TOKEN = process.env.JWT_TOKEN || 'your-jwt-token-here';

if (JWT_TOKEN === 'your-jwt-token-here') {
    console.error('❌ Please set JWT_TOKEN environment variable or update the script');
    console.log('💡 Get token from: curl -X POST http://localhost:8080/iam/auth/login ...');
    process.exit(1);
}

// Helper class for API testing
class APITester {
    constructor(token) {
        this.token = token;
        this.baseUrl = 'http://localhost:8082/testorder';
    }

    async testCreateOrder() {
        const testData = {
            fullName: 'Node.js Test User',
            email: 'nodejs.test@example.com',
            address: '123 Node.js Test St',
            phoneNumber: '0123456789',
            dateOfBirth: '1990-01-01',
            gender: 'MALE'
        };

        try {
            const response = await fetch(`${this.baseUrl}/testorders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                },
                body: JSON.stringify(testData)
            });

            if (response.ok) {
                const result = await response.json();
                console.log(`🆕 Created test order with ID: ${result.testId}`);
                return result.testId;
            } else {
                console.error(`❌ Failed to create order: ${response.status}`);
                return null;
            }
        } catch (error) {
            console.error(`❌ API Error: ${error.message}`);
            return null;
        }
    }

    async testUpdateOrder(orderId) {
        const updateData = {
            fullName: 'Node.js Test User Updated',
            address: '456 Updated Node.js St',
            phone: '0987654321',
            dateOfBirth: '1990-01-01',
            gender: 'MALE'
        };

        try {
            const response = await fetch(`${this.baseUrl}/testorders/${orderId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                },
                body: JSON.stringify(updateData)
            });

            if (response.ok) {
                console.log(`📝 Updated test order ${orderId}`);
                return true;
            } else {
                console.error(`❌ Failed to update order: ${response.status}`);
                return false;
            }
        } catch (error) {
            console.error(`❌ API Error: ${error.message}`);
            return false;
        }
    }
}

const tester = new TestOrderWebSocketTester();
const apiTester = new APITester(JWT_TOKEN);

// Handle graceful shutdown
process.on('SIGINT', () => {
    console.log('\n🛑 Shutting down...');
    tester.disconnect();
    process.exit(0);
});

// Enhanced test with API integration
async function runCompleteTest() {
    try {
        console.log('🚀 Starting complete WebSocket + API test...');
        
        // Connect WebSocket
        await tester.connect();
        tester.subscribeToAllTopics();
        
        console.log('\n⏳ Waiting 3 seconds for WebSocket subscriptions...');
        await new Promise(resolve => setTimeout(resolve, 3000));
        
        // Test API calls to trigger notifications
        console.log('\n🧪 Testing API calls...');
        
        const orderId = await apiTester.testCreateOrder();
        if (orderId) {
            await new Promise(resolve => setTimeout(resolve, 2000));
            await apiTester.testUpdateOrder(orderId);
        }
        
        console.log('\n🕰️  Listening for more notifications for 60 seconds...');
        console.log('💡 You can also manually create/update orders via other tools');
        
        await new Promise(resolve => setTimeout(resolve, 60000));
        
        console.log(`\n📊 Test completed. Received ${tester.messageCount} WebSocket messages.`);
        tester.disconnect();
        
    } catch (error) {
        console.error(`❌ Test failed: ${error.message}`);
        tester.disconnect();
        process.exit(1);
    }
}

// Start enhanced test
runCompleteTest().then(() => {
    console.log('✅ Complete test finished successfully');
    process.exit(0);
}).catch((error) => {
    console.error('❌ Test failed:', error);
    process.exit(1);
});
```

### 2.3 Chạy Node.js Test

```bash
# Bước 1: Get JWT Token
JWT_TOKEN=$(curl -s -X POST http://localhost:8080/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your-username","password":"your-password"}' | \
  jq -r '.access_token')

# Bước 2: Run test with token
JWT_TOKEN=$JWT_TOKEN node test-websocket.js

# Hoặc set environment variable
export JWT_TOKEN="your-actual-jwt-token-here"
node test-websocket.js

# Hoặc inline
JWT_TOKEN="your-actual-jwt-token-here" node test-websocket.js
```

## Method 3: Python Testing Script

### 3.1 Setup Python Environment

```bash
# Install dependencies
pip install websocket-client stomp.py requests
```

### 3.2 Python Test Script

Tạo file `test_websocket.py`:

```python
#!/usr/bin/env python3
import json
import time
import threading
import websocket
import stomp
from datetime import datetime

class TestOrderWebSocketTester:
    def __init__(self):
        self.ws = None
        self.connected = False
        self.message_count = 0
        
    def on_open(self, ws):
        print("✅ WebSocket connection opened")
        self.connected = True
        
        # Send STOMP CONNECT frame
        connect_frame = "CONNECT\naccept-version:1.0,1.1,2.0\n\n\x00"
        ws.send(connect_frame)
        
    def on_message(self, ws, message):
        print(f"📨 Raw message received: {message}")
        
        if message.startswith("CONNECTED"):
            print("✅ STOMP connection established")
            # Subscribe to topics
            self.subscribe_to_topics()
            
        elif message.startswith("MESSAGE"):
            self.handle_notification(message)
            
    def on_error(self, ws, error):
        print(f"❌ WebSocket error: {error}")
        
    def on_close(self, ws, close_status_code, close_msg):
        print("🔌 WebSocket connection closed")
        self.connected = False
        
    def subscribe_to_topics(self):
        topics = [
            "/topic/testorder",
            "/topic/testorder/created", 
            "/topic/testorder/updated",
            "/topic/testorder/status",
            "/topic/testorder/results",
            "/topic/testorder/urgent"
        ]
        
        for i, topic in enumerate(topics):
            subscribe_frame = f"SUBSCRIBE\nid:sub-{i}\ndestination:{topic}\n\n\x00"
            self.ws.send(subscribe_frame)
            print(f"📡 Subscribed to {topic}")
            
    def handle_notification(self, message):
        try:
            # Parse STOMP message
            lines = message.split('\n')
            body_start = message.find('\n\n') + 2
            body = message[body_start:].rstrip('\x00')
            
            if body:
                notification = json.loads(body)
                self.message_count += 1
                
                print(f"\n📨 Notification #{self.message_count} received:")
                print(f"🕒 Time: {datetime.now().isoformat()}")
                print(f"📝 Title: {notification.get('title', 'N/A')}")
                print(f"💬 Message: {notification.get('message', 'N/A')}")
                print(f"🎯 Action: {notification.get('action', 'N/A')}")
                print(f"🆔 Entity ID: {notification.get('entityId', 'N/A')}")
                print(f"🏷️  Event ID: {notification.get('eventId', 'N/A')}")
                print(f"👥 Target Privileges: {notification.get('targetPrivileges', [])}")
                print(f"🌐 Is Global: {notification.get('isGlobal', False)}")
                print(f"📊 Order Status: {notification.get('orderStatus', 'N/A')}")
                print(f"⚡ Priority: {notification.get('priority', 'N/A')}")
                print("─" * 50)
                
        except json.JSONDecodeError as e:
            print(f"❌ Failed to parse notification JSON: {e}")
        except Exception as e:
            print(f"❌ Error handling notification: {e}")
            
    def connect(self):
        print("🔌 Connecting to TestOrder WebSocket...")
        
        # Use SockJS endpoint
        url = "ws://localhost:8082/testorder/ws/websocket"
        
        self.ws = websocket.WebSocketApp(
            url,
            on_open=self.on_open,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
        
    def run_test(self, duration=60):
        self.connect()
        
        # Start WebSocket in separate thread
        wst = threading.Thread(target=self.ws.run_forever)
        wst.daemon = True
        wst.start()
        
        print(f"\n🕰️  Listening for notifications for {duration} seconds...")
        print("💡 Now create/update test orders to see notifications!")
        print("🛠️  Use REST API endpoints to trigger notifications")
        
        try:
            time.sleep(duration)
        except KeyboardInterrupt:
            print("\n🛑 Test interrupted by user")
            
        print(f"\n📊 Test completed. Received {self.message_count} messages.")
        self.ws.close()

if __name__ == "__main__":
    tester = TestOrderWebSocketTester()
    tester.run_test(120)  # Listen for 2 minutes
```

### 3.3 Chạy Python Test

```bash
python test_websocket.py
```

## Method 4: REST API Trigger Tests

### 4.1 Test Script để Trigger Notifications

Tạo file `trigger-notifications.sh`:

```bash
#!/bin/bash

# Configuration
BASE_URL="http://localhost:8082/testorder"
TOKEN="your-jwt-token-here"  # Replace with actual JWT token

echo "🚀 Starting notification trigger tests..."

# Function to make authenticated API calls
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    if [ "$method" = "POST" ] || [ "$method" = "PUT" ]; then
        curl -s -X $method \
             -H "Authorization: Bearer $TOKEN" \
             -H "Content-Type: application/json" \
             -d "$data" \
             "$BASE_URL$endpoint"
    else
        curl -s -X $method \
             -H "Authorization: Bearer $TOKEN" \
             "$BASE_URL$endpoint"
    fi
}

# Test 1: Create Test Order (triggers created notification)
echo "📝 Test 1: Creating test order..."
CREATE_DATA='{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "address": "123 Main St",
    "phoneNumber": "0123456789",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE"
}'

RESPONSE=$(make_request "POST" "/testorders" "$CREATE_DATA")
echo "Response: $RESPONSE"

# Extract test order ID from response (assuming JSON response)
TEST_ORDER_ID=$(echo $RESPONSE | grep -o '"testId":[0-9]*' | grep -o '[0-9]*')
echo "Created Test Order ID: $TEST_ORDER_ID"

sleep 2

# Test 2: Update Test Order (triggers updated notification)
if [ ! -z "$TEST_ORDER_ID" ]; then
    echo "📝 Test 2: Updating test order $TEST_ORDER_ID..."
    UPDATE_DATA='{
        "fullName": "John Doe Updated",
        "address": "456 Updated St",
        "phone": "0987654321",
        "dateOfBirth": "1990-01-01",
        "gender": "MALE"
    }'
    
    UPDATE_RESPONSE=$(make_request "PUT" "/testorders/$TEST_ORDER_ID" "$UPDATE_DATA")
    echo "Update Response: $UPDATE_RESPONSE"
    
    sleep 2
fi

# Test 3: Check notifications via REST API
echo "📧 Test 3: Checking notifications via REST API..."

echo "Getting all notifications:"
make_request "GET" "/notifications" | jq '.' 2>/dev/null || echo "Response received (jq not available for formatting)"

echo "Getting unread count:"
make_request "GET" "/notifications/unread-count"

echo "Getting paginated notifications:"
make_request "GET" "/notifications/paging?page=0&size=5" | jq '.' 2>/dev/null || echo "Response received"

echo "✅ Notification trigger tests completed!"
```

### 4.2 PowerShell Version cho Windows

Tạo file `trigger-notifications.ps1`:

```powershell
# Configuration
$BaseUrl = "http://localhost:8082/testorder"
$Token = "your-jwt-token-here"  # Replace with actual JWT token

Write-Host "🚀 Starting notification trigger tests..." -ForegroundColor Green

# Function to make authenticated API calls
function Make-Request {
    param(
        [string]$Method,
        [string]$Endpoint,
        [string]$Data = $null
    )
    
    $headers = @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
    
    $uri = "$BaseUrl$Endpoint"
    
    try {
        if ($Data) {
            $response = Invoke-RestMethod -Uri $uri -Method $Method -Headers $headers -Body $Data
        } else {
            $response = Invoke-RestMethod -Uri $uri -Method $Method -Headers $headers
        }
        return $response
    } catch {
        Write-Host "❌ Request failed: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Test 1: Create Test Order
Write-Host "📝 Test 1: Creating test order..." -ForegroundColor Blue

$createData = @{
    fullName = "John Doe"
    email = "john.doe@example.com"
    address = "123 Main St"
    phoneNumber = "0123456789"
    dateOfBirth = "1990-01-01"
    gender = "MALE"
} | ConvertTo-Json

$createResponse = Make-Request -Method "POST" -Endpoint "/testorders" -Data $createData

if ($createResponse) {
    Write-Host "✅ Test order created successfully" -ForegroundColor Green
    $testOrderId = $createResponse.testId
    Write-Host "Test Order ID: $testOrderId" -ForegroundColor Yellow
    
    Start-Sleep -Seconds 2
    
    # Test 2: Update Test Order
    Write-Host "📝 Test 2: Updating test order $testOrderId..." -ForegroundColor Blue
    
    $updateData = @{
        fullName = "John Doe Updated"
        address = "456 Updated St"
        phone = "0987654321"
        dateOfBirth = "1990-01-01"
        gender = "MALE"
    } | ConvertTo-Json
    
    $updateResponse = Make-Request -Method "PUT" -Endpoint "/testorders/$testOrderId" -Data $updateData
    
    if ($updateResponse) {
        Write-Host "✅ Test order updated successfully" -ForegroundColor Green
    }
    
    Start-Sleep -Seconds 2
}

# Test 3: Check notifications
Write-Host "📧 Test 3: Checking notifications via REST API..." -ForegroundColor Blue

Write-Host "Getting all notifications:"
$notifications = Make-Request -Method "GET" -Endpoint "/notifications"
if ($notifications) {
    $notifications | ConvertTo-Json -Depth 3
}

Write-Host "Getting unread count:"
$unreadCount = Make-Request -Method "GET" -Endpoint "/notifications/unread-count"
Write-Host "Unread count: $unreadCount" -ForegroundColor Yellow

Write-Host "Getting paginated notifications:"
$paginatedNotifications = Make-Request -Method "GET" -Endpoint "/notifications/paging?page=0&size=5"
if ($paginatedNotifications) {
    Write-Host "Total elements: $($paginatedNotifications.totalElements)" -ForegroundColor Yellow
    Write-Host "Number of elements: $($paginatedNotifications.numberOfElements)" -ForegroundColor Yellow
}

Write-Host "✅ Notification trigger tests completed!" -ForegroundColor Green
```

## Method 5: Automated Integration Test

### 5.1 Jest Test Suite

Tạo file `websocket.integration.test.js`:

```javascript
const SockJS = require('sockjs-client');
const { Client } = require('@stomp/stompjs');
const axios = require('axios');

// Polyfill cho Node.js
Object.assign(global, { WebSocket: require('ws') });

describe('TestOrder WebSocket Integration Tests', () => {
    let stompClient;
    let receivedMessages = [];
    const BASE_URL = 'http://localhost:8082/testorder';
    const JWT_TOKEN = process.env.JWT_TOKEN;

    beforeAll(() => {
        if (!JWT_TOKEN) {
            throw new Error('JWT_TOKEN environment variable is required for testing');
        }
    });
    
    beforeAll(async () => {
        // Setup WebSocket connection
        await new Promise((resolve, reject) => {
            const socket = new SockJS(`${BASE_URL}/ws`);
            stompClient = new Client({
                webSocketFactory: () => socket,
                onConnect: () => {
                    console.log('✅ WebSocket connected for testing');
                    resolve();
                },
                onStompError: (frame) => {
                    reject(new Error(`STOMP error: ${frame.headers['message']}`));
                }
            });
            stompClient.activate();
        });
        
        // Subscribe to test topic
        stompClient.subscribe('/topic/testorder', (message) => {
            receivedMessages.push(JSON.parse(message.body));
        });
    });
    
    afterAll(() => {
        if (stompClient) {
            stompClient.deactivate();
        }
    });
    
    beforeEach(() => {
        receivedMessages = []; // Clear messages before each test
    });
    
    test('should receive notification when test order is created', async () => {
        const testOrderData = {
            fullName: 'Test User',
            email: 'test@example.com',
            address: '123 Test St',
            phoneNumber: '0123456789',
            dateOfBirth: '1990-01-01',
            gender: 'MALE'
        };
        
        // Create test order via REST API
        const response = await axios.post(`${BASE_URL}/testorders`, testOrderData, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        });
        
        expect(response.status).toBe(200);
        
        // Wait for WebSocket notification
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // Verify notification received
        expect(receivedMessages.length).toBeGreaterThan(0);
        
        const notification = receivedMessages.find(msg => msg.action === 'CREATE');
        expect(notification).toBeDefined();
        expect(notification.title).toBe('New Test Order Created');
        expect(notification.entityType).toBe('TEST_ORDER');
        expect(notification.isRead).toBe(false);
    });
    
    test('should receive notification when test order is updated', async () => {
        // First create a test order
        const createData = {
            fullName: 'Update Test User',
            email: 'update@example.com',
            address: '123 Update St',
            phoneNumber: '0123456789',
            dateOfBirth: '1990-01-01',
            gender: 'FEMALE'
        };
        
        const createResponse = await axios.post(`${BASE_URL}/testorders`, createData, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        });
        
        const testOrderId = createResponse.data.testId;
        
        // Wait a bit then clear messages
        await new Promise(resolve => setTimeout(resolve, 1000));
        receivedMessages = [];
        
        // Update the test order
        const updateData = {
            fullName: 'Updated Test User',
            address: '456 Updated St',
            phone: '0987654321',
            dateOfBirth: '1990-01-01',
            gender: 'FEMALE'
        };
        
        const updateResponse = await axios.put(`${BASE_URL}/testorders/${testOrderId}`, updateData, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        });
        
        expect(updateResponse.status).toBe(200);
        
        // Wait for WebSocket notification
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // Verify update notification received
        const updateNotification = receivedMessages.find(msg => msg.action === 'UPDATE');
        expect(updateNotification).toBeDefined();
        expect(updateNotification.title).toBe('Test Order Updated');
        expect(updateNotification.entityId).toBe(testOrderId.toString());
    });
    
    test('should be able to retrieve notifications via REST API', async () => {
        const response = await axios.get(`${BASE_URL}/notifications`, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        });
        
        expect(response.status).toBe(200);
        expect(Array.isArray(response.data)).toBe(true);
    });
    
    test('should get correct unread count', async () => {
        const response = await axios.get(`${BASE_URL}/notifications/unread-count`, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        });
        
        expect(response.status).toBe(200);
        expect(typeof response.data).toBe('number');
        expect(response.data).toBeGreaterThanOrEqual(0);
    });
});
```

### 5.2 Chạy Integration Tests

```bash
# Setup
npm install --save-dev jest axios sockjs-client @stomp/stompjs ws

# Add to package.json
{
  "scripts": {
    "test:websocket": "jest websocket.integration.test.js --detectOpenHandles"
  }
}

# Get JWT Token and run tests
JWT_TOKEN=$(curl -s -X POST http://localhost:8080/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your-username","password":"your-password"}' | \
  jq -r '.access_token')

JWT_TOKEN=$JWT_TOKEN npm run test:websocket

# Hoặc set environment variable permanent
export JWT_TOKEN="your-actual-jwt-token-here"
npm run test:websocket
```

### 5.3 Quick Token Helper Script

Tạo file `get-token.js` để lấy JWT token dễ dàng:

```javascript
const axios = require('axios');

async function getToken(username, password) {
    try {
        const response = await axios.post('http://localhost:8080/iam/auth/login', {
            username: username,
            password: password
        });
        
        console.log('✅ Login successful');
        console.log(`🔑 JWT Token: ${response.data.access_token}`);
        console.log(`⏰ Expires in: ${response.data.expires_in} seconds`);
        
        return response.data.access_token;
    } catch (error) {
        console.error('❌ Login failed:', error.response?.data || error.message);
        return null;
    }
}

// Usage
if (require.main === module) {
    const username = process.argv[2] || 'testuser';
    const password = process.argv[3] || 'testpass123';
    
    getToken(username, password);
}

module.exports = { getToken };
```

```bash
# Use helper script
node get-token.js your-username your-password

# Copy token từ output và sử dụng trong tests
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   ```
   ❌ Error: connect ECONNREFUSED 127.0.0.1:8082
   ```
   - Kiểm tra TestOrder service có đang chạy không
   - Verify port 8082 có available không

2. **CORS Error**
   ```
   ❌ Access-Control-Allow-Origin error
   ```
   - Kiểm tra `WebSocketConfig.java` có correct origin settings
   - Thử thay đổi origin thành `*` để test

3. **STOMP Connection Failed**
   ```
   ❌ STOMP error: Connection failed
   ```
   - Kiểm tra WebSocket endpoint URL
   - Verify SockJS fallback hoạt động

4. **No Notifications Received**
   ```
   ✅ Connected but no messages
   ```
   - Kiểm tra RabbitMQ có đang chạy không
   - Verify notification queues được tạo
   - Check MongoDB connection
   - Verify user privileges

5. **Authentication Issues**
   ```
   ❌ 401 Unauthorized
   ```
   - Kiểm tra JWT token có valid không
   - Verify token format: `Bearer <token>`
   - Check token expiry: `echo "token" | cut -d'.' -f2 | base64 -d | jq '.exp'`
   - Ensure IAM service đang chạy trên port 8080
   - Verify user có đủ privileges (1L: View, 2L: Manage, 3L: Modify)

6. **Token Expired**
   ```
   ❌ JWT token has expired
   ```
   - Get new token từ IAM service
   - Check thời gian hệ thống có đồng bộ không
   - Consider using refresh token nếu available

7. **CORS Issues với Authentication**
   ```
   ❌ CORS error with Authorization header
   ```
   - Verify WebSocketConfig có allow origins đúng
   - Check SecurityConfig có allow preflight requests
   - Ensure Authorization header được include trong CORS config

### Debug Commands

```bash
# Check service health với authentication
JWT_TOKEN="your-jwt-token-here"
curl -H "Authorization: Bearer $JWT_TOKEN" \
     http://localhost:8082/testorder/notifications/health

# Check WebSocket endpoint
curl -I http://localhost:8082/testorder/ws

# Check IAM service
curl http://localhost:8080/iam/health

# Validate JWT token
curl -H "Authorization: Bearer $JWT_TOKEN" \
     http://localhost:8080/iam/auth/validate

# Check RabbitMQ queues
curl -u guest:guest http://localhost:15672/api/queues | \
     jq '.[] | select(.name | contains("testorder.notification"))'

# Check MongoDB notifications
mongo --eval "db.testorderMessage.find().limit(5).pretty()" testorder_notifications

# Quick login test
curl -X POST http://localhost:8080/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'

# Decode JWT token (check expiry)
echo "$JWT_TOKEN" | cut -d'.' -f2 | base64 -d | jq '.'
```

## Expected Results

Khi test thành công, bạn sẽ thấy:

1. **WebSocket Connection**: `Connected` status trong console
2. **Subscription Success**: Confirmation messages cho mỗi topic subscription  
3. **Real-time Notifications**: Messages xuất hiện ngay khi create/update test orders
4. **Correct Data**: Notification objects chứa đầy đủ thông tin (title, message, action, etc.)
5. **Privilege Filtering**: Chỉ nhận notifications phù hợp với user privileges
6. **Multiple Topics**: Messages xuất hiện trên đúng topics (/topic/testorder/created, etc.)

Nếu tất cả tests pass, WebSocket notification system hoạt động đúng và ready for production!