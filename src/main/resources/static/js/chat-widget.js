/**
 * SportZone Chat Widget – SockJS + STOMP Client
 * Handles WebSocket connection, message sending/receiving, and UI updates.
 */
(function () {
  'use strict';

  // ─── State ──────────────────────────────────────────
  let stompClient = null;
  let conversationId = null;
  let currentUserId = null;
  let isConnected = false;
  let isOpen = false;
  let unreadCount = 0;

  // ─── DOM refs ───────────────────────────────────────
  const fab        = document.getElementById('chatFab');
  const badge      = document.getElementById('chatBadge');
  const chatWin    = document.getElementById('chatWindow');
  const msgArea    = document.getElementById('chatMessages');
  const inputEl    = document.getElementById('chatInput');
  const sendBtn    = document.getElementById('chatSendBtn');
  const connStatus = document.getElementById('chatConnStatus');

  if (!fab || !chatWin) return; // widget not in DOM

  // ─── Toggle Chat Window ─────────────────────────────
  fab.addEventListener('click', () => {
    isOpen = !isOpen;
    fab.classList.toggle('open', isOpen);
    chatWin.classList.toggle('visible', isOpen);

    if (isOpen) {
      // Clear badge
      unreadCount = 0;
      updateBadge();

      // Check login
      const userJson = sessionStorage.getItem('currentUser');
      if (!userJson) {
        showLoginPrompt();
        return;
      }
      const user = JSON.parse(userJson);
      currentUserId = user.id;

      hideLoginPrompt();
      initConversation();
    }
  });

  // ─── Utils for Badge ────────────────────────────────
  function updateBadge() {
    if (!badge) return;
    if (unreadCount > 0) {
      badge.textContent = unreadCount > 9 ? '9+' : unreadCount;
      badge.classList.remove('hidden');
    } else {
      badge.classList.add('hidden');
    }
  }

  // ─── Send Message ───────────────────────────────────
  function sendMessage() {
    const text = inputEl.value.trim();
    if (!text || !isConnected || !conversationId) return;

    const payload = {
      senderId: currentUserId,
      conversationId: conversationId,
      content: text
    };

    stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(payload)
    });

    inputEl.value = '';
    inputEl.focus();
  }

  if (sendBtn) sendBtn.addEventListener('click', sendMessage);
  if (inputEl) {
    inputEl.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
      }
    });
  }

  // ─── Init Conversation ──────────────────────────────
  async function initConversation() {
    if (!isConnected) {
      setConnStatus('connecting');
    }
    try {
      const res = await fetch('/api/chat/my-conversation');
      if (!res.ok) {
        showLoginPrompt();
        return;
      }
      const conv = await res.json();
      conversationId = conv.id;
      
      // Update initial unread count if chat is closed (though it's usually open when init is called)
      if (!isOpen && conv.unreadCount > 0) {
        unreadCount = conv.unreadCount;
        updateBadge();
      }

      // Load history
      await loadHistory(conversationId);

      // Connect WebSocket
      connectWebSocket();
    } catch (err) {
      console.error('Chat init error:', err);
      if (!isConnected) setConnStatus('error');
    }
  }

  // ─── Load Chat History ──────────────────────────────
  async function loadHistory(convId) {
    try {
      const res = await fetch('/api/chat/history/' + convId);
      if (!res.ok) return;
      const messages = await res.json();

      // Clear existing messages (keep welcome if no messages)
      msgArea.innerHTML = '';

      if (messages.length === 0) {
        msgArea.innerHTML = `
          <div class="chat-welcome">
            <div class="chat-welcome-icon">👋</div>
            <p>Xin chào! Chúng tôi sẵn sàng hỗ trợ bạn.<br>Hãy gửi tin nhắn để bắt đầu.</p>
          </div>`;
      } else {
        messages.forEach(msg => appendMessage(msg, false));
      }
      scrollToBottom();
    } catch (err) {
      console.error('Load history error:', err);
    }
  }

  // ─── WebSocket Connection ───────────────────────────
  function connectWebSocket() {
    if (stompClient && isConnected) return;

    const socket = new SockJS('/ws');
    stompClient = new StompJs.Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        isConnected = true;
        setConnStatus(null);

        // Subscribe to conversation messages
        stompClient.subscribe('/topic/conversation/' + conversationId, (message) => {
          const msg = JSON.parse(message.body);
          
          // Only increment unread if chat window is closed AND message is from staff
          if (!isOpen && msg.senderId !== currentUserId) {
            unreadCount++;
            updateBadge();
          }

          appendMessage(msg, true);
          scrollToBottom();
        });
      },
      onDisconnect: () => {
        isConnected = false;
        // Only show connecting if still open
        if (isOpen) setConnStatus('connecting');
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        if (isOpen) setConnStatus('error');
      }
    });

    stompClient.activate();
  }

  // ─── Append Message to UI ──────────────────────────
  function appendMessage(msg, animate) {
    // Remove welcome message if present
    const welcome = msgArea.querySelector('.chat-welcome');
    if (welcome) welcome.remove();

    const isMine = msg.senderId === currentUserId;
    const div = document.createElement('div');
    div.className = 'chat-msg ' + (isMine ? 'sent' : 'received');
    if (!animate) div.style.animation = 'none';

    let senderLabel = '';
    if (!isMine && msg.senderRole !== 'CLIENT') {
      senderLabel = `<div class="chat-msg-sender">${escapeHtml(msg.senderName)} (${msg.senderRole})</div>`;
    }

    const timeStr = formatTime(msg.timestamp);
    div.innerHTML = `
      ${senderLabel}
      <div>${escapeHtml(msg.content)}</div>
      <div class="chat-msg-meta">${timeStr}</div>
    `;
    msgArea.appendChild(div);
  }

  // ─── Utils ──────────────────────────────────────────
  function scrollToBottom() {
    requestAnimationFrame(() => {
      msgArea.scrollTop = msgArea.scrollHeight;
    });
  }

  function formatTime(ts) {
    if (!ts) return '';
    const d = new Date(ts);
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return hh + ':' + mm;
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;');
  }

  function setConnStatus(status) {
    if (!connStatus) return;
    connStatus.className = 'chat-conn-status';
    if (status === 'connecting') {
      connStatus.className += ' connecting';
      connStatus.textContent = 'Đang kết nối...';
    } else if (status === 'error') {
      connStatus.className += ' error';
      connStatus.textContent = 'Mất kết nối. Đang thử lại...';
    }
  }

  function showLoginPrompt() {
    const loginPrompt = document.getElementById('chatLoginPrompt');
    const chatBody = document.getElementById('chatBody');
    if (loginPrompt) loginPrompt.style.display = 'flex';
    if (chatBody) chatBody.style.display = 'none';
  }

  function hideLoginPrompt() {
    const loginPrompt = document.getElementById('chatLoginPrompt');
    const chatBody = document.getElementById('chatBody');
    if (loginPrompt) loginPrompt.style.display = 'none';
    if (chatBody) chatBody.style.display = 'flex';
  }

})();
