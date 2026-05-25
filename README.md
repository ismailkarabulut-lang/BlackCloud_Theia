<!DOCTYPE html>
<html lang="tr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>BlackCloud_Theia — Android Mimarisi v4.0</title>
<style>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;600;700;900&family=JetBrains+Mono:wght@300;400;500&family=Rajdhani:wght@300;400;500;600&display=swap');

:root {
  --bg: #03020a;
  --surface: rgba(255,255,255,0.03);
  --border: rgba(255,255,255,0.07);
  --text: rgba(255,255,255,0.85);
  --muted: rgba(255,255,255,0.35);
  --dim: rgba(255,255,255,0.12);
  --purple: #7C6EF5;
  --green: #1D9E75;
  --orange: #EF9F27;
  --red: #E85D24;
  --blue: #4FA8D5;
  --pink: #C77DFF;
  --gold: #E8B86D;
  --teal: #2DD4BF;
}
* { margin:0; padding:0; box-sizing:border-box; }
html { scroll-behavior:smooth; }
body {
  background: var(--bg);
  color: var(--text);
  font-family: 'Rajdhani', sans-serif;
  font-size: 15px;
  line-height: 1.7;
  min-height: 100vh;
}
body::before {
  content:'';
  position:fixed; inset:0;
  background:
    radial-gradient(ellipse at 10% 15%, rgba(124,110,245,0.07) 0%, transparent 50%),
    radial-gradient(ellipse at 90% 80%, rgba(45,212,191,0.05) 0%, transparent 45%),
    radial-gradient(ellipse at 50% 50%, rgba(10,6,25,1) 0%, transparent 80%);
  pointer-events:none; z-index:0;
}
.container { max-width:1200px; margin:0 auto; padding:52px 28px; position:relative; z-index:1; }

/* HEADER */
.header { text-align:center; margin-bottom:64px; padding-bottom:40px; border-bottom:1px solid var(--border); position:relative; }
.header::after { content:''; position:absolute; bottom:-1px; left:50%; transform:translateX(-50%); width:180px; height:1px; background:linear-gradient(90deg,transparent,var(--purple),transparent); }
.eyebrow { font-family:'JetBrains Mono',monospace; font-size:9px; letter-spacing:.38em; color:var(--purple); text-transform:uppercase; margin-bottom:16px; display:flex; align-items:center; justify-content:center; gap:12px; }
.eyebrow::before,.eyebrow::after { content:''; width:36px; height:1px; background:linear-gradient(90deg,transparent,var(--purple)); }
.eyebrow::after { transform:scaleX(-1); }
h1 { font-family:'Orbitron',sans-serif; font-size:clamp(26px,4.5vw,44px); font-weight:900; letter-spacing:.1em; color:#fff; text-shadow:0 0 60px rgba(124,110,245,0.4); margin-bottom:10px; }
h1 span { color:var(--purple); }
.header-sub { font-size:15px; color:var(--muted); letter-spacing:.05em; margin-bottom:28px; }
.badges { display:flex; gap:7px; justify-content:center; flex-wrap:wrap; }
.badge-item { font-family:'JetBrains Mono',monospace; font-size:9.5px; letter-spacing:.1em; padding:4px 13px; border-radius:20px; border:1px solid var(--border); background:var(--surface); color:var(--muted); }
.badge-item.on { border-color:rgba(29,158,117,0.4); color:var(--green); background:rgba(29,158,117,0.07); }
.badge-item.warn { border-color:rgba(239,159,39,0.35); color:var(--orange); background:rgba(239,159,39,0.06); }
.badge-item.alert { border-color:rgba(232,93,36,0.4); color:var(--red); background:rgba(232,93,36,0.07); }
.badge-item.concept { border-color:rgba(79,168,213,0.35); color:var(--blue); background:rgba(79,168,213,0.06); }

/* SECTION */
.section { margin-bottom:60px; }
.sec-title { font-family:'Orbitron',sans-serif; font-size:10px; font-weight:600; letter-spacing:.3em; color:var(--muted); text-transform:uppercase; margin-bottom:22px; display:flex; align-items:center; gap:13px; }
.sec-title::after { content:''; flex:1; height:1px; background:linear-gradient(90deg,var(--border),transparent); }
.sec-num { width:22px; height:22px; border-radius:5px; background:var(--surface); border:1px solid var(--border); display:flex; align-items:center; justify-content:center; font-size:8px; color:var(--dim); }

/* GRAPH */
.graph-wrap { position:relative; width:100%; height:700px; background:var(--surface); border:1px solid var(--border); border-radius:18px; overflow:hidden; }
.graph-wrap svg { position:absolute; inset:0; width:100%; height:100%; }
.graph-node { position:absolute; transform:translate(-50%,-50%); cursor:pointer; z-index:2; }
.gn-box { padding:10px 16px; border-radius:11px; border:1px solid var(--border); background:linear-gradient(145deg,rgba(255,255,255,0.055),rgba(255,255,255,0.015)); backdrop-filter:blur(12px); text-align:center; white-space:nowrap; transition:all 0.3s cubic-bezier(0.23,1,0.32,1); box-shadow:0 4px 20px rgba(0,0,0,0.4); }
.gn-box:hover { transform:translateY(-3px) scale(1.06); box-shadow:0 8px 32px rgba(0,0,0,0.5), 0 0 40px var(--nc,rgba(124,110,245,0.2)); border-color:var(--nc,rgba(124,110,245,0.4)); }
.gn-label { font-family:'Orbitron',sans-serif; font-size:9px; font-weight:700; letter-spacing:.18em; text-transform:uppercase; }
.gn-sub { font-family:'JetBrains Mono',monospace; font-size:8px; color:var(--muted); letter-spacing:.06em; margin-top:2px; }
.gn-purple .gn-box { border-color:rgba(124,110,245,0.35); background:rgba(124,110,245,0.1); --nc:rgba(124,110,245,0.4); } .gn-purple .gn-label { color:var(--purple); }
.gn-green  .gn-box { border-color:rgba(29,158,117,0.35);  background:rgba(29,158,117,0.09);  --nc:rgba(29,158,117,0.4);  } .gn-green  .gn-label { color:var(--green); }
.gn-orange .gn-box { border-color:rgba(239,159,39,0.35);  background:rgba(239,159,39,0.08);  --nc:rgba(239,159,39,0.4);  } .gn-orange .gn-label { color:var(--orange); }
.gn-blue   .gn-box { border-color:rgba(79,168,213,0.35);  background:rgba(79,168,213,0.09);  --nc:rgba(79,168,213,0.4);  } .gn-blue   .gn-label { color:var(--blue); }
.gn-pink   .gn-box { border-color:rgba(199,125,255,0.35); background:rgba(199,125,255,0.09); --nc:rgba(199,125,255,0.4); } .gn-pink   .gn-label { color:var(--pink); }
.gn-gold   .gn-box { border-color:rgba(232,184,109,0.35); background:rgba(232,184,109,0.08); --nc:rgba(232,184,109,0.4); } .gn-gold   .gn-label { color:var(--gold); }
.gn-teal   .gn-box { border-color:rgba(45,212,191,0.4);   background:rgba(45,212,191,0.1);   --nc:rgba(45,212,191,0.4);  } .gn-teal   .gn-label { color:var(--teal); }
.gn-red    .gn-box { border-color:rgba(232,93,36,0.45);   background:rgba(232,93,36,0.12);   --nc:rgba(232,93,36,0.5);   } .gn-red    .gn-label { color:var(--red); }
.gn-core .gn-box { padding:16px 22px; border-radius:14px; border-color:rgba(124,110,245,0.5); background:radial-gradient(circle,rgba(124,110,245,0.18),rgba(124,110,245,0.06)); animation:corePulse 3s ease-in-out infinite; }
@keyframes corePulse { 0%,100% { box-shadow:0 0 60px rgba(124,110,245,0.3),0 0 120px rgba(124,110,245,0.1); } 50% { box-shadow:0 0 80px rgba(124,110,245,0.5),0 0 160px rgba(124,110,245,0.2); } }

/* RETRIEVAL benzeri — Room + History Mimarisi */
.retrieval-box { background:var(--surface); border:1px solid var(--border); border-radius:14px; padding:24px 28px; }
.retrieval-intro { font-size:13px; color:var(--muted); margin-bottom:20px; line-height:1.7; }
.retrieval-grid { display:grid; grid-template-columns:1fr 40px 1fr; gap:16px; align-items:start; }
.ret-col { display:flex; flex-direction:column; gap:10px; }
.ret-layer { padding:12px 14px; border-radius:9px; border:1px solid var(--border); font-family:'JetBrains Mono',monospace; font-size:11px; line-height:1.8; }
.ret-layer .rl-title { font-weight:500; margin-bottom:4px; font-size:12px; }
.ret-layer .rl-sub { font-size:10px; color:var(--muted); }
.ret-layer.room   { border-color:rgba(45,212,191,0.3);  background:rgba(45,212,191,0.05);  } .ret-layer.room   .rl-title { color:var(--teal); }
.ret-layer.history { border-color:rgba(239,159,39,0.3);  background:rgba(239,159,39,0.05);  } .ret-layer.history .rl-title { color:var(--orange); }
.ret-layer.result { border-color:rgba(124,110,245,0.35); background:rgba(124,110,245,0.07); } .ret-layer.result .rl-title { color:var(--purple); }
.ret-plus { display:flex; align-items:center; justify-content:center; font-family:'Orbitron',sans-serif; font-size:24px; color:var(--dim); padding-top:40px; }

/* CARDS */
.cards { display:grid; grid-template-columns:repeat(auto-fill,minmax(290px,1fr)); gap:14px; }
.card { background:var(--surface); border:1px solid var(--border); border-radius:12px; padding:18px; transition:all 0.25s ease; position:relative; overflow:hidden; }
.card::before { content:''; position:absolute; top:0; left:0; width:3px; height:100%; background:var(--cc,var(--purple)); opacity:0.6; }
.card:hover { border-color:var(--cc,rgba(124,110,245,0.4)); transform:translateY(-2px); box-shadow:0 6px 24px rgba(0,0,0,0.3); }
.card-head { display:flex; align-items:center; justify-content:space-between; margin-bottom:10px; }
.card-title { font-family:'Orbitron',sans-serif; font-size:11px; font-weight:700; letter-spacing:.18em; color:var(--cc,var(--purple)); text-transform:uppercase; }
.card-tag { font-family:'JetBrains Mono',monospace; font-size:8px; letter-spacing:.1em; padding:2px 8px; border-radius:10px; border:1px solid currentColor; }
.card-tag.on   { color:var(--green); }
.card-tag.warn { color:var(--orange); }
.card-body { font-size:13px; color:var(--text); line-height:1.6; }
.card-body code { font-family:'JetBrains Mono',monospace; font-size:11px; background:rgba(255,255,255,0.05); padding:1px 6px; border-radius:3px; color:var(--gold); }
.card-body strong { font-weight:600; color:#fff; }
.card-meta { margin-top:10px; padding-top:10px; border-top:1px dashed var(--border); font-family:'JetBrains Mono',monospace; font-size:10px; color:var(--muted); line-height:1.7; }

/* TIMELINE */
.timeline { position:relative; padding-left:24px; }
.timeline::before { content:''; position:absolute; left:7px; top:8px; bottom:8px; width:1px; background:linear-gradient(180deg,var(--purple),transparent); }
.tl-item { position:relative; padding:8px 0 8px 18px; }
.tl-item::before { content:''; position:absolute; left:-21px; top:14px; width:9px; height:9px; border-radius:50%; background:var(--cc,var(--green)); box-shadow:0 0 12px var(--cc,var(--green)); }
.tl-item.done    { --cc:var(--green); }
.tl-item.partial { --cc:var(--orange); }
.tl-item.todo    { --cc:var(--muted); }
.tl-text { font-size:13.5px; color:var(--text); }
.tl-text code { font-family:'JetBrains Mono',monospace; font-size:11px; background:rgba(255,255,255,0.05); padding:1px 5px; border-radius:3px; color:var(--gold); }

/* CMD */
.cmd-strip { background:rgba(0,0,0,0.4); border:1px solid var(--border); border-radius:10px; padding:14px 18px; font-family:'JetBrains Mono',monospace; font-size:11.5px; color:var(--muted); line-height:2; }
.cmd-strip .prompt  { color:var(--green); }
.cmd-strip .cmd     { color:var(--text); }
.cmd-strip .comment { color:var(--dim); font-style:italic; }

/* TAB SWITCHER */
.tab-switcher { background:var(--surface); border:1px solid var(--border); border-radius:14px; overflow:hidden; }
.tab-nav { display:flex; border-bottom:1px solid var(--border); background:rgba(0,0,0,0.2); overflow-x:auto; }
.tab-btn {
  font-family:'JetBrains Mono',monospace; font-size:11px; letter-spacing:.06em;
  padding:12px 20px; cursor:pointer; white-space:nowrap; color:var(--muted);
  background:transparent; border:none; border-bottom:2px solid transparent;
  transition:all 0.25s ease; position:relative;
}
.tab-btn:hover { color:var(--text); background:rgba(255,255,255,0.03); }
.tab-btn.active { color:var(--purple); border-bottom-color:var(--purple); background:rgba(124,110,245,0.05); }
.tab-pane { display:none; padding:0; position:relative; }
.tab-pane.active { display:block; }
.code-block-wrap { position:relative; }
.copy-btn {
  position:absolute; top:12px; right:14px; z-index:10;
  font-family:'JetBrains Mono',monospace; font-size:10px; letter-spacing:.06em;
  padding:4px 10px; border-radius:6px; cursor:pointer;
  background:rgba(124,110,245,0.15); border:1px solid rgba(124,110,245,0.3);
  color:var(--purple); transition:all 0.2s ease;
}
.copy-btn:hover { background:rgba(124,110,245,0.25); }
.copy-btn.copied { color:var(--green); border-color:rgba(29,158,117,0.4); background:rgba(29,158,117,0.1); }
pre.code-block {
  font-family:'JetBrains Mono',monospace; font-size:12px; line-height:1.9;
  background:rgba(0,0,0,0.45); padding:24px 28px; margin:0; overflow-x:auto;
  color:rgba(255,255,255,0.75);
}
.kw   { color:#7C6EF5; font-weight:500; }
.fn   { color:#4FA8D5; }
.str  { color:#E8B86D; }
.num  { color:#2DD4BF; }
.cm   { color:rgba(255,255,255,0.28); font-style:italic; }
.dec  { color:#C77DFF; }
.cls  { color:#EF9F27; }
.op   { color:rgba(255,255,255,0.45); }

/* LAYER STACK */
.layer-stack { display:flex; flex-direction:column; gap:0; }
.layer-item {
  position:relative; display:flex; align-items:stretch;
  border:1px solid var(--border); border-radius:12px;
  background:var(--surface); overflow:hidden;
  opacity:0; transform:translateX(-40px);
  transition:opacity 0.6s ease, transform 0.6s ease;
}
.layer-item.visible { opacity:1; transform:translateX(0); }
.layer-accent { width:5px; flex-shrink:0; background:var(--lc,var(--purple)); }
.layer-body { padding:18px 22px; flex:1; }
.layer-name { font-family:'Orbitron',sans-serif; font-size:10px; font-weight:700; letter-spacing:.25em; color:var(--lc,var(--purple)); text-transform:uppercase; margin-bottom:10px; }
.layer-pills { display:flex; flex-wrap:wrap; gap:7px; }
.layer-pill {
  font-family:'JetBrains Mono',monospace; font-size:10px; letter-spacing:.05em;
  padding:3px 10px; border-radius:20px; border:1px solid rgba(255,255,255,0.1);
  background:rgba(255,255,255,0.04); color:var(--muted);
  transition:all 0.2s ease;
}
.layer-pill:hover { border-color:var(--lc,var(--purple)); color:var(--text); background:rgba(255,255,255,0.07); }
.layer-pill.planned { border-style:dashed; opacity:0.6; }
.layer-arrow {
  display:flex; justify-content:center; align-items:center;
  padding:6px 0; position:relative;
}
.layer-arrow::before {
  content:''; position:absolute; left:50%; top:0; bottom:0; width:1px;
  background:linear-gradient(180deg, var(--border), transparent);
  transform:translateX(-50%);
}
.arrow-dot {
  width:24px; height:24px; border-radius:50%;
  background:rgba(255,255,255,0.03); border:1px solid var(--border);
  display:flex; align-items:center; justify-content:center;
  font-size:12px; color:var(--dim); z-index:1;
  animation:arrowBounce 2s ease-in-out infinite;
}
@keyframes arrowBounce { 0%,100% { transform:translateY(0); } 50% { transform:translateY(3px); } }

.footer { text-align:center; padding:30px 0 10px; font-family:'JetBrains Mono',monospace; font-size:10px; color:var(--dim); letter-spacing:.2em; border-top:1px dashed var(--border); margin-top:60px; }

@media (max-width:760px) {
  .graph-wrap { height:820px; }
  h1 { font-size:32px; }
  .retrieval-grid { grid-template-columns:1fr; }
  .tab-btn { font-size:10px; padding:10px 14px; }
}
</style>
</head>
<body>
<div class="container">

  <!-- HEADER -->
  <div class="header">
    <div class="eyebrow">Android · MVVM · Compose</div>
    <h1>BlackCloud<span>_</span>Theia</h1>
    <div class="header-sub">v4.0 · Jetpack Compose + FastAPI + Gemini · Hafızalı Asistan</div>
    <div class="badges">
      <span class="badge-item on">● UI · Compose · Material3</span>
      <span class="badge-item on">● ViewModel · StateFlow</span>
      <span class="badge-item on">● Repository · Retrofit</span>
      <span class="badge-item on">● Room · SQLite</span>
      <span class="badge-item on">● Voice · SpeechRecognizer + TTS</span>
      <span class="badge-item on">● Foreground Service</span>
      <span class="badge-item on">● Action Dispatcher</span>
      <span class="badge-item on">● Theia Core · FastAPI</span>
      <span class="badge-item on">● Google Gemini API</span>
      <span class="badge-item concept">.env · Config</span>
    </div>
  </div>

  <!-- 01 — TOPOLOJİ -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">01</span>Sistem Topolojisi · Android + Backend</div>
    <div class="graph-wrap" id="graph-container">
      <svg id="graph-svg"></svg>
      <div id="tooltip"></div>
    </div>
    <div class="legend">
      <div class="legend-item"><span class="legend-dot" style="background:#C77DFF"></span>Kullanıcı / Giriş</div>
      <div class="legend-item"><span class="legend-dot" style="background:#4FA8D5"></span>UI · Compose</div>
      <div class="legend-item"><span class="legend-dot" style="background:#7C6EF5"></span>ViewModel / Action</div>
      <div class="legend-item"><span class="legend-dot" style="background:#EF9F27"></span>Repository / Veri</div>
      <div class="legend-item"><span class="legend-dot" style="background:#2DD4BF"></span>Servisler (Voice / FG)</div>
      <div class="legend-item"><span class="legend-dot" style="background:#1D9E75"></span>Backend · Theia Core</div>
      <div class="legend-item"><span class="legend-dot" style="background:#E8B86D"></span>Gemini API</div>
      <div class="legend-item"><span class="legend-dot" style="background:#E85D24"></span>Dispatcher / Event</div>
    </div>
  </div>

  <!-- 02 — HAFIZA MİMARİSİ (Room + HistoryRepository) -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">02</span>Yerel Hafıza Mimarisi · Room & History</div>
    <div class="retrieval-box">
      <div class="retrieval-intro">Uygulama, her konuşmayı ve proje yapılandırmasını <strong>Room (SQLite)</strong> üzerinde saklar. <code>HistoryRepository</code> hem yerel veritabanını hem de API'yi yönetir. Chat geçmişi proje bazlı filtrelenir ve ViewModel'e StateFlow ile sunulur.</div>
      <div class="retrieval-grid">
        <div class="ret-col">
          <div class="ret-layer room">
            <div class="rl-title">Room Database · SQLite</div>
            <div class="rl-sub">Tablo: <code>messages</code> (id, projectId, content, isUser, timestamp)</div>
            <div class="rl-sub">Tablo: <code>projects</code> (id, name, config)</div>
            <div class="rl-sub">Tablo: <code>config</code> (key, value)</div>
          </div>
          <div class="ret-layer history">
            <div class="rl-title">HistoryRepository</div>
            <div class="rl-sub">Room DAO + Remote API (Theia Core) senkronizasyonu</div>
            <div class="rl-sub">Tüm mesajlar önce local'e yazılır, ardından API'ye gönderilir</div>
            <div class="rl-sub">Flow ile canlı güncellemeler</div>
          </div>
        </div>
        <div class="ret-plus">+</div>
        <div class="ret-col">
          <div class="ret-layer result">
            <div class="rl-title">ViewModel'e Sunulan State</div>
            <div class="rl-sub"><code>StateFlow&lt;List&lt;Message&gt;&gt;</code> → UI otomatik yenilenir</div>
            <div class="rl-sub">Proje değiştiğinde otomatik geçmiş yüklenir</div>
            <div class="rl-sub">Çevrimdışı modda local veri ile çalışır, bağlantı gelince senkronize eder</div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 03 — BİLEŞENLER -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">03</span>Bileşenler · Android + Backend</div>
    <div class="cards">
      <div class="card" style="--cc:#4FA8D5">
        <div class="card-head"><div class="card-title">UI · Jetpack Compose</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">ProjectSwitcherScreen, ChatWorkspaceScreen, MessageBubble, VoiceFab, ModelsMainframeScreen, ConfigConsoleScreen. Material3 tema, dark mode, responsive.</div>
        <div class="card-meta">path: <code>app/src/main/java/com/blackcloud/theia/ui/</code></div>
      </div>
      <div class="card" style="--cc:#7C6EF5">
        <div class="card-head"><div class="card-title">BlackCloudViewModel</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">StateFlow ile UI state yönetimi. Repository, VoiceManager, ActionDispatcher, ForegroundService referanslarını tutar. Coroutine + Flow ile asenkron işlemler.</div>
        <div class="card-meta">path: <code>.../viewmodel/BlackCloudViewModel.kt</code></div>
      </div>
      <div class="card" style="--cc:#EF9F27">
        <div class="card-head"><div class="card-title">TheiaRepository</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Retrofit + TheiaApiClient ile FastAPI'ye bağlanır. HistoryRepository ile local/remote senkronizasyonu sağlar. SSE (Server‑Sent Events) desteği.</div>
        <div class="card-meta">path: <code>.../repository/TheiaRepository.kt</code></div>
      </div>
      <div class="card" style="--cc:#2DD4BF">
        <div class="card-head"><div class="card-title">VoiceInputManager</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Android SpeechRecognizer ile gerçek zamanlı ses tanıma. Sonuçları Flow ile ViewModel'e iletir. "Dinle" butonu ile tetiklenir.</div>
        <div class="card-meta">path: <code>.../voice/VoiceInputManager.kt</code></div>
      </div>
      <div class="card" style="--cc:#2DD4BF">
        <div class="card-head"><div class="card-title">VoiceOutputManager</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Android TextToSpeech ile asistan yanıtlarını seslendirir. Hız ve dil ayarları ConfigConsoleScreen'den yapılır.</div>
        <div class="card-meta">path: <code>.../voice/VoiceOutputManager.kt</code></div>
      </div>
      <div class="card" style="--cc:#1D9E75">
        <div class="card-head"><div class="card-title">BlackCloudForegroundService</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Uygulama arka plandayken Theia Core bağlantısını canlı tutar. Bildirim ile kullanıcıya bilgi verir. Service başlatma ViewModel üzerinden yapılır.</div>
        <div class="card-meta">path: <code>.../service/BlackCloudForegroundService.kt</code></div>
      </div>
      <div class="card" style="--cc:#E85D24">
        <div class="card-head"><div class="card-title">ActionDispatcher</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Kullanıcı aksiyonlarını (mesaj gönderme, proje değiştirme, sesli giriş, yapılandırma) merkezi bir kanaldan ViewModel'e iletir. UI'dan bağımsız test edilebilir.</div>
        <div class="card-meta">path: <code>.../dispatcher/ActionDispatcher.kt</code></div>
      </div>
      <div class="card" style="--cc:#EF9F27">
        <div class="card-head"><div class="card-title">HistoryRepository + Room</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">Room DAO ile mesaj geçmişini SQLite'ta tutar. Proje bazlı sorgular, timestamp sıralama, sayfalama desteği. Remote API'ye yedekleme.</div>
        <div class="card-meta">path: <code>.../database/</code> + <code>.../repository/HistoryRepository.kt</code></div>
      </div>
      <div class="card" style="--cc:#7C6EF5">
        <div class="card-head"><div class="card-title">Theia Core (FastAPI)</div><div class="card-tag on">YEREL</div></div>
        <div class="card-body">Localhost'ta çalışan FastAPI sunucusu. Android uygulaması Retrofit ile bu sunucuya POST/GET yapar. Sunucu, Gemini API'ye yönlendirir ve hafıza yönetimi yapar.</div>
        <div class="card-meta">url: <code>http://localhost:8000</code> (emulator için 10.0.2.2:8000)</div>
      </div>
      <div class="card" style="--cc:#E8B86D">
        <div class="card-head"><div class="card-title">Google Gemini API</div><div class="card-tag on">BULUT</div></div>
        <div class="card-body">Theia Core üzerinden çağrılır. API key <code>.env</code> dosyasında saklanır. Prompt içine geçmiş mesajlar ve proje bağlamı eklenir.</div>
        <div class="card-meta">model: gemini-1.5-flash veya pro</div>
      </div>
      <div class="card" style="--cc:#4FA8D5; opacity:0.7;">
        <div class="card-head"><div class="card-title">ConfigConsoleScreen</div><div class="card-tag on">AKTİF</div></div>
        <div class="card-body">API URL, TTS hızı, varsayılan model, tema gibi ayarları SharedPreferences'a kaydeder. LiveData/Flow ile ViewModel'e bildirir.</div>
        <div class="card-meta">path: <code>.../ui/config/ConfigConsoleScreen.kt</code></div>
      </div>
    </div>
  </div>

  <!-- 04 — TIMELINE (Proje Gelişim) -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">04</span>Gelişim Zaman Çizelgesi</div>
    <div class="timeline">
      <div class="tl-item done"><div class="tl-text">Temel MVVM + Compose kurulumu, ProjectSwitcherScreen</div></div>
      <div class="tl-item done"><div class="tl-text">Retrofit entegrasyonu, TheiaCore ile bağlantı, mesaj gönderme/alma</div></div>
      <div class="tl-item done"><div class="tl-text">Room Database + HistoryRepository: mesajlar local'de saklanıyor</div></div>
      <div class="tl-item done"><div class="tl-text">Voice Input (SpeechRecognizer) ve Output (TTS) eklendi, VoiceFab butonu</div></div>
      <div class="tl-item done"><div class="tl-text">Foreground Service ile arka plan bağlantı kalıcılığı sağlandı</div></div>
      <div class="tl-item done"><div class="tl-text">ActionDispatcher ile UI aksiyonları merkezi hale getirildi</div></div>
      <div class="tl-item done"><div class="tl-text">ModelsMainframeScreen: farklı AI modelleri arasında geçiş (Theia Core üzerinden)</div></div>
      <div class="tl-item partial"><div class="tl-text">ConfigConsoleScreen: API URL, TTS, tema dinamik ayarları tamamlandı</div></div>
      <div class="tl-item todo"><div class="tl-text">SSE ile streaming yanıt desteği (Theia Core'da implementasyonu bekliyor)</div></div>
      <div class="tl-item todo"><div class="tl-text">Otomatik gece/gündüz teması</div></div>
      <div class="tl-item todo"><div class="tl-text">Widget (hızlı sesli komut) ve bildirim yanıtlama</div></div>
    </div>
  </div>

  <!-- 05 — KOMUT REFERANSI -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">05</span>Komut Referansı · Build & Run</div>
    <div class="cmd-strip">
<span class="comment"># Projeyi klonla</span><br>
<span class="prompt">$</span> <span class="cmd">git clone https://github.com/ismailkarabulut-lang/BlackCloud_Theia.git</span><br>
<br>
<span class="comment"># .env dosyasını oluştur ve Gemini API key ekle</span><br>
<span class="prompt">$</span> <span class="cmd">cp .env.example .env</span><br>
<span class="prompt">$</span> <span class="cmd">nano .env   # GEMINI_API_KEY=...</span><br>
<br>
<span class="comment"># Debug apk build et</span><br>
<span class="prompt">$</span> <span class="cmd">./gradlew assembleDebug</span><br>
<br>
<span class="comment"># Testleri çalıştır</span><br>
<span class="prompt">$</span> <span class="cmd">./gradlew test</span><br>
<br>
<span class="comment"># Emulator veya fiziksel cihaza yükle</span><br>
<span class="prompt">$</span> <span class="cmd">adb install app/build/outputs/apk/debug/app-debug.apk</span><br>
<br>
<span class="comment"># Logcat ile canlı log izle (uygulama paket adı: com.blackcloud.theia)</span><br>
<span class="prompt">$</span> <span class="cmd">adb logcat -s BlackCloud_Theia</span><br>
<br>
<span class="comment"># Theia Core'u (FastAPI) lokal çalıştır (ayrı repo)</span><br>
<span class="prompt">$</span> <span class="cmd">cd ~/theia-core</span><br>
<span class="prompt">$</span> <span class="cmd">source venv/bin/activate && python main.py</span><br>
    </div>
  </div>

  <!-- 06 — KRİTİK KOTLIN DOSYALARI (Tab) -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">06</span>Kritik Kod Parçaları · İskelet</div>
    <div class="tab-switcher">
      <div class="tab-nav">
        <button class="tab-btn active" data-tab="viewmodel">BlackCloudViewModel.kt</button>
        <button class="tab-btn" data-tab="repository">TheiaRepository.kt</button>
        <button class="tab-btn" data-tab="history">HistoryRepository.kt</button>
        <button class="tab-btn" data-tab="voice">VoiceInputManager.kt</button>
      </div>

      <!-- ViewModel -->
      <div class="tab-pane active" id="tab-viewmodel">
        <div class="code-block-wrap">
          <button class="copy-btn" onclick="copyCode(this)">copy</button>
          <pre class="code-block"><span class="cm">// BlackCloudViewModel.kt</span>
<span class="kw">class</span> <span class="cls">BlackCloudViewModel</span>(
    <span class="kw">private val</span> repository: <span class="cls">TheiaRepository</span>,
    <span class="kw">private val</span> historyRepo: <span class="cls">HistoryRepository</span>,
    <span class="kw">private val</span> voiceInput: <span class="cls">VoiceInputManager</span>,
    <span class="kw">private val</span> voiceOutput: <span class="cls">VoiceOutputManager</span>,
    <span class="kw">private val</span> dispatcher: <span class="cls">ActionDispatcher</span>
) : <span class="cls">ViewModel</span>() {

    <span class="cm">// UI State</span>
    <span class="kw">private val</span> _messages = <span class="cls">MutableStateFlow</span>&lt;<span class="cls">List</span>&lt;<span class="cls">Message</span>&gt;&gt;(emptyList())
    <span class="kw">val</span> messages: <span class="cls">StateFlow</span>&lt;<span class="cls">List</span>&lt;<span class="cls">Message</span>&gt;&gt; = _messages.asStateFlow()

    <span class="kw">private val</span> _isLoading = <span class="cls">MutableStateFlow</span>(<span class="num">false</span>)
    <span class="kw">val</span> isLoading = _isLoading.asStateFlow()

    init {
        <span class="fn">viewModelScope.launch</span> {
            historyRepo.<span class="fn">getAllMessages</span>().<span class="fn">collect</span> { _messages.value = it }
        }
        <span class="fn">observeActions</span>()
    }

    <span class="kw">private fun</span> <span class="fn">observeActions</span>() {
        <span class="fn">viewModelScope.launch</span> {
            dispatcher.actions.<span class="fn">collect</span> { action ->
                <span class="kw">when</span> (action) {
                    <span class="kw">is</span> <span class="cls">Action.SendMessage</span> -> <span class="fn">sendMessage</span>(action.text)
                    <span class="kw">is</span> <span class="cls">Action.SwitchProject</span> -> <span class="fn">switchProject</span>(action.projectId)
                    <span class="kw">is</span> <span class="cls">Action.VoiceInputToggle</span> -> <span class="fn">toggleVoiceInput</span>()
                }
            }
        }
    }

    <span class="kw">private fun</span> <span class="fn">sendMessage</span>(text: <span class="cls">String</span>) {
        <span class="fn">viewModelScope.launch</span> {
            _isLoading.value = <span class="num">true</span>
            <span class="kw">val</span> response = repository.<span class="fn">sendMessage</span>(text)
            historyRepo.<span class="fn">saveMessage</span>(<span class="cls">Message</span>(isUser = <span class="num">false</span>, content = response))
            _isLoading.value = <span class="num">false</span>
            voiceOutput.<span class="fn">speak</span>(response)
        }
    }
}</pre>
        </div>
      </div>

      <!-- TheiaRepository -->
      <div class="tab-pane" id="tab-repository">
        <div class="code-block-wrap">
          <button class="copy-btn" onclick="copyCode(this)">copy</button>
          <pre class="code-block"><span class="cm">// TheiaRepository.kt</span>
<span class="kw">class</span> <span class="cls">TheiaRepository</span>(
    <span class="kw">private val</span> api: <span class="cls">TheiaApiClient</span>
) {
    <span class="kw">suspend fun</span> <span class="fn">sendMessage</span>(message: <span class="cls">String</span>): <span class="cls">String</span> {
        <span class="kw">return try</span> {
            <span class="kw">val</span> response = api.<span class="fn">postMessage</span>(<span class="cls">MessageRequest</span>(message))
            response.reply
        } <span class="kw">catch</span> (e: <span class="cls">Exception</span>) {
            <span class="str">"Bağlantı hatası: Theia Core çalışmıyor olabilir."</span>
        }
    }

    <span class="kw">suspend fun</span> <span class="fn">getModels</span>(): <span class="cls">List</span>&lt;<span class="cls">ModelInfo</span>&gt; = api.<span class="fn">getModels</span>()
    <span class="kw">suspend fun</span> <span class="fn">setConfig</span>(key: <span class="cls">String</span>, value: <span class="cls">String</span>) = api.<span class="fn">updateConfig</span>(key, value)
}</pre>
        </div>
      </div>

      <!-- HistoryRepository -->
      <div class="tab-pane" id="tab-history">
        <div class="code-block-wrap">
          <button class="copy-btn" onclick="copyCode(this)">copy</button>
          <pre class="code-block"><span class="cm">// HistoryRepository.kt (Room)</span>
<span class="kw">class</span> <span class="cls">HistoryRepository</span>(
    <span class="kw">private val</span> messageDao: <span class="cls">MessageDao</span>
) {
    <span class="kw">fun</span> <span class="fn">getAllMessages</span>(projectId: <span class="cls">String</span>? = <span class="num">null</span>): <span class="cls">Flow</span>&lt;<span class="cls">List</span>&lt;<span class="cls">Message</span>&gt;&gt; {
        <span class="kw">return if</span> (projectId != <span class="num">null</span>) messageDao.<span class="fn">getMessagesByProject</span>(projectId)
        <span class="kw">else</span> messageDao.<span class="fn">getAllMessages</span>()
    }

    <span class="kw">suspend fun</span> <span class="fn">saveMessage</span>(message: <span class="cls">Message</span>) {
        messageDao.<span class="fn">insert</span>(message)
    }

    <span class="kw">suspend fun</span> <span class="fn">deleteProjectHistory</span>(projectId: <span class="cls">String</span>) {
        messageDao.<span class="fn">deleteByProject</span>(projectId)
    }
}</pre>
        </div>
      </div>

      <!-- VoiceInputManager -->
      <div class="tab-pane" id="tab-voice">
        <div class="code-block-wrap">
          <button class="copy-btn" onclick="copyCode(this)">copy</button>
          <pre class="code-block"><span class="cm">// VoiceInputManager.kt</span>
<span class="kw">class</span> <span class="cls">VoiceInputManager</span>(
    <span class="kw">private val</span> context: <span class="cls">Context</span>
) {
    <span class="kw">private val</span> _recognizedText = <span class="cls">MutableStateFlow</span>(<span class="str">""</span>)
    <span class="kw">val</span> recognizedText: <span class="cls">StateFlow</span>&lt;<span class="cls">String</span>&gt; = _recognizedText.asStateFlow()

    <span class="kw">private var</span> speechRecognizer: <span class="cls">SpeechRecognizer</span>? = <span class="num">null</span>
    <span class="kw">private val</span> speechListener = <span class="fn">object</span> : <span class="cls">RecognitionListener</span> {
        <span class="kw">override fun</span> <span class="fn">onResults</span>(results: <span class="cls">Bundle</span>) {
            <span class="kw">val</span> matches = results.<span class="fn">getStringArrayList</span>(<span class="cls">SpeechRecognizer</span>.<span class="cls">RESULTS_RECOGNITION</span>)
            _recognizedText.value = matches?.<span class="fn">firstOrNull</span>() ?: <span class="str">""</span>
        }
    }

    <span class="kw">fun</span> <span class="fn">startListening</span>() {
        speechRecognizer = <span class="cls">SpeechRecognizer</span>.<span class="fn">createSpeechRecognizer</span>(context)
        speechRecognizer?.<span class="fn">setRecognitionListener</span>(speechListener)
        <span class="kw">val</span> intent = <span class="cls">Intent</span>(<span class="cls">RecognizerIntent</span>.<span class="cls">ACTION_RECOGNIZE_SPEECH</span>).<span class="fn">apply</span> {
            <span class="fn">putExtra</span>(<span class="cls">RecognizerIntent</span>.<span class="cls">EXTRA_LANGUAGE_MODEL</span>, <span class="cls">RecognizerIntent</span>.<span class="cls">LANGUAGE_MODEL_FREE_FORM</span>)
        }
        speechRecognizer?.<span class="fn">startListening</span>(intent)
    }
}</pre>
        </div>
      </div>
    </div>
  </div>

  <!-- 07 — MİMARİ KATMANLAR -->
  <div class="section">
    <div class="sec-title"><span class="sec-num">07</span>Mimari Katmanlar · Veri Akış Yönü</div>
    <div class="layer-stack" id="layer-stack">

      <div class="layer-item" style="--lc:#C77DFF;" data-delay="0">
        <div class="layer-accent"></div>
        <div class="layer-body">
          <div class="layer-name">Kullanıcı Arayüzü (UI)</div>
          <div class="layer-pills">
            <span class="layer-pill">ProjectSwitcherScreen</span>
            <span class="layer-pill">ChatWorkspaceScreen</span>
            <span class="layer-pill">MessageBubble</span>
            <span class="layer-pill">VoiceFab</span>
            <span class="layer-pill">ModelsMainframeScreen</span>
            <span class="layer-pill">ConfigConsoleScreen</span>
          </div>
        </div>
      </div>

      <div class="layer-arrow"><div class="arrow-dot">↓</div></div>

      <div class="layer-item" style="--lc:#7C6EF5;" data-delay="150">
        <div class="layer-accent"></div>
        <div class="layer-body">
          <div class="layer-name">ViewModel + Action Dispatcher</div>
          <div class="layer-pills">
            <span class="layer-pill">BlackCloudViewModel</span>
            <span class="layer-pill">StateFlow</span>
            <span class="layer-pill">ActionDispatcher</span>
            <span class="layer-pill">Coroutine · viewModelScope</span>
          </div>
        </div>
      </div>

      <div class="layer-arrow"><div class="arrow-dot">↓</div></div>

      <div class="layer-item" style="--lc:#EF9F27;" data-delay="300">
        <div class="layer-accent"></div>
        <div class="layer-body">
          <div class="layer-name">Repository Katmanı</div>
          <div class="layer-pills">
            <span class="layer-pill">TheiaRepository</span>
            <span class="layer-pill">HistoryRepository</span>
            <span class="layer-pill">Room · MessageDao</span>
            <span class="layer-pill">Retrofit · TheiaApiClient</span>
          </div>
        </div>
      </div>

      <div class="layer-arrow"><div class="arrow-dot">↓</div></div>

      <div class="layer-item" style="--lc:#2DD4BF;" data-delay="450">
        <div class="layer-accent"></div>
        <div class="layer-body">
          <div class="layer-name">Servisler ve Altyapı</div>
          <div class="layer-pills">
            <span class="layer-pill">ForegroundService</span>
            <span class="layer-pill">VoiceInputManager · SpeechRecognizer</span>
            <span class="layer-pill">VoiceOutputManager · TTS</span>
            <span class="layer-pill">SharedPreferences · Config</span>
          </div>
        </div>
      </div>

      <div class="layer-arrow"><div class="arrow-dot">↓</div></div>

      <div class="layer-item" style="--lc:#1D9E75;" data-delay="600">
        <div class="layer-accent"></div>
        <div class="layer-body">
          <div class="layer-name">Backend · Theia Core</div>
          <div class="layer-pills">
            <span class="layer-pill">FastAPI · localhost:8000</span>
            <span class="layer-pill">Google Gemini API</span>
            <span class="layer-pill">.env · API Key</span>
          </div>
        </div>
      </div>

    </div>
  </div>

  <div class="footer">BlackCloud_Theia · Android Mimarisi · v4.0 · Jetpack Compose + Theia Core + Gemini</div>

</div>

<script>
/* ── GRAPH DATA ─────────────────────────────────────────────────── */
const NODES = [
  { id:'user',       label:'KULLANICI',        sub:'Dokunmatik / Ses',            color:'#C77DFF', x:0.50, y:0.05 },
  { id:'ui',         label:'COMPOSE UI',       sub:'Ekranlar + State',            color:'#4FA8D5', x:0.50, y:0.18 },
  { id:'vm',         label:'VIEWMODEL',        sub:'BlackCloudViewModel',         color:'#7C6EF5', x:0.50, y:0.32, isCore:true },
  { id:'dispatcher', label:'ACTION DISP.',     sub:'Aksiyon merkezi',             color:'#E85D24', x:0.28, y:0.32 },
  { id:'repo',       label:'REPOSITORY',       sub:'TheiaRepository',             color:'#EF9F27', x:0.50, y:0.48 },
  { id:'history',    label:'HISTORY REPO',     sub:'Room + DAO',                  color:'#EF9F27', x:0.15, y:0.48 },
  { id:'room',       label:'ROOM DB',          sub:'SQLite · messages',           color:'#EF9F27', x:0.15, y:0.65 },
  { id:'voice_in',   label:'VOICE INPUT',      sub:'SpeechRecognizer',            color:'#2DD4BF', x:0.82, y:0.60 },
  { id:'voice_out',  label:'VOICE OUTPUT',     sub:'TextToSpeech',                color:'#2DD4BF', x:0.82, y:0.45 },
  { id:'fg',         label:'FOREGROUND SRV',   sub:'Arka plan bağlantısı',        color:'#2DD4BF', x:0.82, y:0.32 },
  { id:'api',        label:'THEIA CORE',       sub:'FastAPI · localhost',         color:'#1D9E75', x:0.50, y:0.80 },
  { id:'gemini',     label:'GEMINI API',       sub:'Google · bulut',              color:'#E8B86D', x:0.50, y:0.95 },
];

const EDGES = [
  { from:'user',     to:'ui',          label:'dokunma / ses' },
  { from:'ui',       to:'vm',          label:'StateFlow tüketir' },
  { from:'ui',       to:'dispatcher',  label:'Action gönderir' },
  { from:'dispatcher', to:'vm',        label:'Action iletir' },
  { from:'vm',       to:'repo',        label:'sendMessage() çağrısı' },
  { from:'vm',       to:'voice_in',    label:'startListening()' },
  { from:'vm',       to:'voice_out',   label:'speak()' },
  { from:'vm',       to:'fg',          label:'service başlat/durdur' },
  { from:'repo',     to:'api',         label:'Retrofit POST /chat' },
  { from:'repo',     to:'history',     label:'saveMessage()' },
  { from:'history',  to:'room',        label:'Room insert/query' },
  { from:'api',      to:'gemini',      label:'forward prompt' },
  { from:'gemini',   to:'api',         label:'yanıt döner' },
  { from:'api',      to:'repo',        label:'response' },
  { from:'voice_in', to:'vm',          label:'recognizedText Flow' },
];

/* ── GRAPH RENDER ───────────────────────────────────────────────── */
const container = document.getElementById('graph-container');
const svg       = document.getElementById('graph-svg');
const tooltip   = document.getElementById('tooltip');

function renderGraph() {
  document.querySelectorAll('.graph-node').forEach(e => e.remove());
  svg.innerHTML = '';
  const W = container.offsetWidth, H = container.offsetHeight;
  svg.setAttribute('viewBox', `0 0 ${W} ${H}`);

  EDGES.forEach((e, idx) => {
    const a = NODES.find(n => n.id === e.from), b = NODES.find(n => n.id === e.to);
    if (!a || !b) return;
    const x1=a.x*W, y1=a.y*H, x2=b.x*W, y2=b.y*H;
    const dx=x2-x1, dy=y2-y1;
    const col = a.color;
    const r=parseInt(col.slice(1,3),16), g=parseInt(col.slice(3,5),16), bb=parseInt(col.slice(5,7),16);
    const pid='p_'+e.from+'_'+e.to+'_'+idx;
    const path=document.createElementNS('http://www.w3.org/2000/svg','path');
    path.setAttribute('id',pid);
    path.setAttribute('d',`M ${x1} ${y1} C ${x1+dx*.2-dy*.08} ${y1+dy*.2+dx*.08}, ${x1+dx*.8+dy*.08} ${y1+dy*.8-dx*.08}, ${x2} ${y2}`);
    path.setAttribute('stroke',`rgba(${r},${g},${bb},0.22)`);
    path.setAttribute('stroke-width', '1.3');
    path.setAttribute('fill','none');
    path.style.cursor='pointer'; path.style.pointerEvents='stroke';
    path.addEventListener('mouseenter',()=>{
      path.setAttribute('stroke',`rgba(${r},${g},${bb},0.9)`);
      path.setAttribute('stroke-width','2.2');
      tooltip.style.opacity='1'; tooltip.textContent=e.label;
    });
    path.addEventListener('mousemove',ev=>{
      const rc=container.getBoundingClientRect();
      tooltip.style.left=(ev.clientX-rc.left+14)+'px';
      tooltip.style.top=(ev.clientY-rc.top-30)+'px';
    });
    path.addEventListener('mouseleave',()=>{
      path.setAttribute('stroke',`rgba(${r},${g},${bb},0.22)`);
      path.setAttribute('stroke-width','1.3');
      tooltip.style.opacity='0';
    });
    svg.appendChild(path);

    const circle=document.createElementNS('http://www.w3.org/2000/svg','circle');
    circle.setAttribute('r','3');
    circle.setAttribute('fill',`rgba(${r},${g},${bb},0.9)`);
    circle.setAttribute('filter',`drop-shadow(0 0 4px rgba(${r},${g},${bb},0.8))`);
    const anim=document.createElementNS('http://www.w3.org/2000/svg','animateMotion');
    anim.setAttribute('dur',(2.5+Math.random()*2.5)+'s');
    anim.setAttribute('repeatCount','indefinite');
    anim.setAttribute('begin',(Math.random()*3)+'s');
    const mpath=document.createElementNS('http://www.w3.org/2000/svg','mpath');
    mpath.setAttribute('href','#'+pid);
    anim.appendChild(mpath); circle.appendChild(anim); svg.appendChild(circle);
  });

  NODES.forEach(n=>{
    const x=n.x*W, y=n.y*H, el=document.createElement('div');
    const cm={'#C77DFF':'pink','#4FA8D5':'blue','#7C6EF5':'purple','#EF9F27':'orange','#2DD4BF':'teal','#1D9E75':'green','#E85D24':'red','#E8B86D':'gold'};
    const colorClass = cm[n.color]||'purple';
    el.className='graph-node gn-'+colorClass+(n.isCore?' gn-core':'');
    el.style.left=x+'px'; el.style.top=y+'px';
    const c=n.color, r2=parseInt(c.slice(1,3),16), g2=parseInt(c.slice(3,5),16), b2=parseInt(c.slice(5,7),16);
    el.innerHTML=`<div class="gn-box" style="border-color:rgba(${r2},${g2},${b2},0.4);background:rgba(${r2},${g2},${b2},${n.isCore?0.18:0.09});--nc:rgba(${r2},${g2},${b2},0.5);"><div class="gn-label" style="color:${c};">${n.label}</div><div class="gn-sub">${n.sub}</div></div>`;
    container.appendChild(el);
  });
}

renderGraph();
let rt; window.addEventListener('resize',()=>{ clearTimeout(rt); rt=setTimeout(renderGraph,150); });

/* ── TAB SWITCHER ───────────────────────────────────────────────── */
document.querySelectorAll('.tab-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    const target = btn.dataset.tab;
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById('tab-' + target).classList.add('active');
  });
});

/* ── COPY BUTTON ────────────────────────────────────────────────── */
function copyCode(btn) {
  const pre = btn.nextElementSibling;
  const text = pre.innerText;
  navigator.clipboard.writeText(text).then(() => {
    btn.textContent = '✓';
    btn.classList.add('copied');
    setTimeout(() => { btn.textContent = 'copy'; btn.classList.remove('copied'); }, 2000);
  });
}

/* ── LAYER STACK — IntersectionObserver ─────────────────────────── */
const layerItems = document.querySelectorAll('.layer-item');
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      const delay = parseInt(entry.target.dataset.delay) || 0;
      setTimeout(() => entry.target.classList.add('visible'), delay);
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.15 });

layerItems.forEach(item => observer.observe(item));
</script>
</body>
</html>
