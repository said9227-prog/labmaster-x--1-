package com.example.ui.screens

import android.graphics.Color as AndroidColor
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeJsBloodTubesScreen(
    onTestClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isWebGlError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أنابيب سحب الدم والفحوصات 3D (Three.js)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { webViewInstance?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("threejs_blood_tubes_webview"),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        setBackgroundColor(AndroidColor.parseColor("#0F172A"))

                        webViewClient = object : WebViewClient() {
                            override fun onRenderProcessGone(view: WebView?, detail: android.webkit.RenderProcessGoneDetail?): Boolean {
                                view?.let {
                                    val parent = it.parent as? android.view.ViewGroup
                                    parent?.removeView(it)
                                    it.destroy()
                                }
                                isWebGlError = true
                                return true
                            }
                        }
                        webChromeClient = WebChromeClient()

                        loadDataWithBaseURL(
                            "https://appassets.androidview",
                            generateBloodTubesThreeJsHtml(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )

            if (isWebGlError) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("عذراً، حدث تعذر في محرك الرسم ثلاثي الأبعاد 3D للأنابيب", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            isWebGlError = false
                            webViewInstance?.reload()
                        }) {
                            Text("إعادة تحميل المشهد 3D")
                        }
                    }
                }
            }
        }
    }
}

private fun generateBloodTubesThreeJsHtml(): String {
    return """
        <!DOCTYPE html>
        <html lang="ar" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <title>3D Blood Tubes & Tests Atlas</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
                body {
                    background-color: #0b1329;
                    color: #f1f5f9;
                    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    overflow-x: hidden;
                    touch-action: manipulation;
                }
                #canvas-container {
                    width: 100vw;
                    height: 50vh;
                    position: relative;
                    background: radial-gradient(circle at center, #1e293b 0%, #0b1329 100%);
                }
                #canvas-container canvas { width: 100%; height: 100%; display: block; }

                .overlay-badge {
                    position: absolute;
                    top: 12px;
                    right: 12px;
                    background: rgba(15, 23, 42, 0.85);
                    padding: 8px 16px;
                    border-radius: 20px;
                    font-size: 13px;
                    font-weight: bold;
                    border: 1px solid rgba(255,255,255,0.15);
                    backdrop-filter: blur(8px);
                }

                .controls-panel {
                    padding: 16px;
                    background: #0f172a;
                    border-top: 1px solid #1e293b;
                    min-height: 50vh;
                }
                .section-title {
                    font-size: 14px;
                    font-weight: bold;
                    margin-bottom: 10px;
                    color: #38bdf8;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }
                .tube-selector-row {
                    display: flex;
                    gap: 8px;
                    overflow-x: auto;
                    padding-bottom: 10px;
                    margin-bottom: 14px;
                    scrollbar-width: none;
                }
                .tube-chip {
                    flex: 0 0 auto;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    background: #1e293b;
                    border: 1px solid #334155;
                    padding: 8px 14px;
                    border-radius: 20px;
                    font-size: 13px;
                    font-weight: bold;
                    color: #94a3b8;
                    cursor: pointer;
                    transition: all 0.2s ease;
                }
                .tube-chip.active {
                    background: #0284c7;
                    border-color: #38bdf8;
                    color: #ffffff;
                    box-shadow: 0 0 14px rgba(56, 189, 248, 0.35);
                }
                .cap-dot {
                    width: 14px;
                    height: 14px;
                    border-radius: 50%;
                    display: inline-block;
                    border: 1px solid rgba(255,255,255,0.4);
                }

                .detail-card {
                    background: #1e293b;
                    border: 1px solid #334155;
                    border-radius: 14px;
                    padding: 14px;
                    margin-bottom: 12px;
                }
                .info-grid {
                    display: grid;
                    grid-template-columns: repeat(2, 1fr);
                    gap: 10px;
                    margin-bottom: 12px;
                }
                .info-item {
                    background: #0f172a;
                    padding: 10px;
                    border-radius: 10px;
                    border: 1px solid #1e293b;
                }
                .info-label { font-size: 11px; color: #94a3b8; margin-bottom: 4px; }
                .info-val { font-size: 13px; font-weight: bold; color: #f8fafc; }

                .tests-tags {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 6px;
                    margin-top: 8px;
                }
                .test-tag {
                    background: rgba(56, 189, 248, 0.15);
                    color: #38bdf8;
                    border: 1px solid rgba(56, 189, 248, 0.3);
                    padding: 4px 10px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: bold;
                }
            </style>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
        </head>
        <body>
            <div id="canvas-container">
                <div class="overlay-badge" id="tube-badge">أنبوب EDTA 3D (الأرجواني)</div>
            </div>

            <div class="controls-panel">
                <div class="section-title">اختر أنبوب سحب الدم لمعاينته تفاعلياً 3D:</div>
                <div class="tube-selector-row">
                    <div class="tube-chip active" onclick="selectTube('edta')">
                        <span class="cap-dot" style="background: #a855f7;"></span> أنبوب EDTA
                    </div>
                    <div class="tube-chip" onclick="selectTube('citrate')">
                        <span class="cap-dot" style="background: #38bdf8;"></span> سترات الصوديوم
                    </div>
                    <div class="tube-chip" onclick="selectTube('sst')">
                        <span class="cap-dot" style="background: #eab308;"></span> أنبوب الجيل SST
                    </div>
                    <div class="tube-chip" onclick="selectTube('plain')">
                        <span class="cap-dot" style="background: #ef4444;"></span> Plain أحمر
                    </div>
                    <div class="tube-chip" onclick="selectTube('heparin')">
                        <span class="cap-dot" style="background: #22c55e;"></span> الهيبارين أخضر
                    </div>
                    <div class="tube-chip" onclick="selectTube('fluoride')">
                        <span class="cap-dot" style="background: #94a3b8;"></span> فلوريد رمادي
                    </div>
                </div>

                <div class="detail-card">
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">ترتيب السحب المعتمد (CLSI):</div>
                            <div class="info-val" id="order-val">#5 في سلسلة السحب</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">المادة المضافة (Additive):</div>
                            <div class="info-val" id="additive-val">K2 EDTA / K3 EDTA</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">عدد مرات المزج (Inversions):</div>
                            <div class="info-val" id="inversion-val">8 - 10 مرات بحذر</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">مبدأ عمل المادة:</div>
                            <div class="info-val" id="mechanism-val">يرتبط بالكالسيوم Ca2+ للتثبيط</div>
                        </div>
                    </div>

                    <div style="font-size: 13px; font-weight: bold; color: #f8fafc; margin-bottom: 6px;">
                        🔬 أهم فحوصات المختبر المرتبطة بهذا الأنبوب:
                    </div>
                    <div class="tests-tags" id="tests-list"></div>
                </div>
            </div>

            <script>
                const tubesData = {
                    edta: {
                        name: 'أنبوب EDTA (الأرجواني)',
                        capColor: 0xa855f7,
                        liquidColor: 0x881337,
                        order: '#5 في ترتيب السحب (الدم الكامل)',
                        additive: 'K2 EDTA / K3 EDTA',
                        inversions: '8 - 10 مرات',
                        mechanism: 'يرتبط بالكالسيوم Ca2+ لمنع التجلط وحفظ خلايا الدم',
                        tests: ['صورة الدم الكاملة CBC', 'السكر التراكمي HbA1c', 'سرعة الترسيب ESR', 'فحص الملاريا Parasites', 'فصيلة الدم والتوافق Blood Grouping']
                    },
                    citrate: {
                        name: 'أنبوب سترات الصوديوم 3.2% (الأزرق الفاتح)',
                        capColor: 0x38bdf8,
                        liquidColor: 0x9f1239,
                        order: '#2 في ترتيب السحب (تجلط الدم)',
                        additive: 'Sodium Citrate 3.2% (1:9 ratio)',
                        inversions: '3 - 4 مرات فقط (تجنب التنشيط)',
                        mechanism: 'يرتبط بالكالسيوم بشكل عكسي لدراسة عوامل التجلط',
                        tests: ['زمن الخثرة PT / INR', 'زمن الثرومبوبلاستين PTT', 'فحص D-Dimer', 'الفبرينوجين Fibrinogen']
                    },
                    sst: {
                        name: 'أنبوب الجيل الفاصل SST (الأصفر / الذهبي)',
                        capColor: 0xeab308,
                        liquidColor: 0xb91c1c,
                        hasGel: true,
                        order: '#3 في ترتيب السحب (الكيمياء والأمصال)',
                        additive: 'Clot Activator + Inert Polymer Gel',
                        inversions: '5 مرات',
                        mechanism: 'ينشط التجلط ويفصل المصل عن الخلايا بالطرد المركزي',
                        tests: ['وظائف الكبد ALT/AST/ALP', 'وظائف الكلى Creatinine/Urea', 'دهون الدم Lipid Profile', 'هرمونات الغدة الدرقية TSH/T4', 'الفيتامينات Vitamin D/B12']
                    },
                    plain: {
                        name: 'أنبوب المصل بدون مادة (الأحمر Plain)',
                        capColor: 0xef4444,
                        liquidColor: 0x991b1b,
                        order: '#3 في ترتيب السحب (بنك الدم والأمصال)',
                        additive: 'بدون مانع (أو منشط خثرة زجاجي)',
                        inversions: '5 مرات (إذا كان يحتوي منشط)',
                        mechanism: 'يسمح للتخثر الطبيعي للحصول على مصل نقي',
                        tests: ['مطابقة وتوافق الدم Crossmatch', 'الفحوصات المناعية Serology', 'مستويات الأدوية العلاجية Drug Levels']
                    },
                    heparin: {
                        name: 'أنبوب الهيبارين (الأخضر)',
                        capColor: 0x22c55e,
                        liquidColor: 0xbe123c,
                        order: '#4 في ترتيب السحب (بلازما الكيمياء)',
                        additive: 'Lithium Heparin / Sodium Heparin',
                        inversions: '8 - 10 مرات',
                        mechanism: 'يثبط الانزيمات المضادة للثرومبين Antithrombin',
                        tests: ['غازات الدم شرياني ABG', 'شوارد الدم Electrolytes (Na, K, Cl)', 'الإنزيمات الطارئة STAT Chemistry']
                    },
                    fluoride: {
                        name: 'أنبوب الفلوريد / الأوكزالات (الرمادي)',
                        capColor: 0x94a3b8,
                        liquidColor: 0x881337,
                        order: '#6 في ترتيب السحب (الجلوكوز)',
                        additive: 'Sodium Fluoride + Potassium Oxalate',
                        inversions: '8 - 10 مرات',
                        mechanism: 'يثبط التحلل السكري Glycolysis لمنع استهلاك الجلوكوز',
                        tests: ['فحص الجلوكوز العشوائي/الصائم FBG', 'منحنى تحمّل الجلوكوز GTT', 'حمض اللاكتيك Lactic Acid']
                    }
                };

                let scene, camera, renderer, tubeGroup, capMesh, liquidMesh, gelMesh;
                let currentTubeKey = 'edta';
                let isDragging = false;
                let previousMousePosition = { x: 0, y: 0 };

                function init3D() {
                    const container = document.getElementById('canvas-container');
                    const width = container.clientWidth;
                    const height = container.clientHeight;

                    scene = new THREE.Scene();
                    camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
                    camera.position.set(0, 0.5, 9);

                    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
                    renderer.setSize(width, height);
                    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5));
                    container.appendChild(renderer.domElement);

                    // Lights
                    const ambientLight = new THREE.AmbientLight(0xffffff, 0.7);
                    scene.add(ambientLight);

                    const dirLight = new THREE.DirectionalLight(0xffffff, 0.9);
                    dirLight.position.set(4, 8, 6);
                    scene.add(dirLight);

                    const backLight = new THREE.DirectionalLight(0x38bdf8, 0.5);
                    backLight.position.set(-4, -4, -4);
                    scene.add(backLight);

                    tubeGroup = new THREE.Group();
                    scene.add(tubeGroup);

                    build3DTube();
                    update3DTube('edta');

                    // Interaction listeners
                    const dom = renderer.domElement;
                    dom.addEventListener('mousedown', (e) => { isDragging = true; previousMousePosition = { x: e.clientX, y: e.clientY }; });
                    dom.addEventListener('mousemove', (e) => {
                        if (!isDragging) return;
                        const deltaMove = { x: e.clientX - previousMousePosition.x, y: e.clientY - previousMousePosition.y };
                        tubeGroup.rotation.y += deltaMove.x * 0.01;
                        tubeGroup.rotation.x += deltaMove.y * 0.01;
                        previousMousePosition = { x: e.clientX, y: e.clientY };
                    });
                    window.addEventListener('mouseup', () => { isDragging = false; });

                    dom.addEventListener('touchstart', (e) => {
                        if (e.touches.length === 1) {
                            isDragging = true;
                            previousMousePosition = { x: e.touches[0].clientX, y: e.touches[0].clientY };
                        }
                    }, { passive: true });
                    dom.addEventListener('touchmove', (e) => {
                        if (!isDragging || e.touches.length !== 1) return;
                        const deltaMove = { x: e.touches[0].clientX - previousMousePosition.x, y: e.touches[0].clientY - previousMousePosition.y };
                        tubeGroup.rotation.y += deltaMove.x * 0.012;
                        tubeGroup.rotation.x += deltaMove.y * 0.012;
                        previousMousePosition = { x: e.touches[0].clientX, y: e.touches[0].clientY };
                    }, { passive: true });
                    window.addEventListener('touchend', () => { isDragging = false; });

                    animate();
                }

                function build3DTube() {
                    // Glass Cylinder Body
                    const bodyGeo = new THREE.CylinderGeometry(1.1, 1.05, 5.2, 32, 1, true);
                    const glassMat = new THREE.MeshStandardMaterial({
                        color: 0xffffff,
                        roughness: 0.1,
                        metalness: 0.1,
                        transparent: true,
                        opacity: 0.45,
                        side: THREE.DoubleSide
                    });
                    const glassMesh = new THREE.Mesh(bodyGeo, glassMat);
                    tubeGroup.add(glassMesh);

                    // Glass Bottom Hemispherical Cap
                    const bottomGeo = new THREE.SphereGeometry(1.05, 32, 16, 0, Math.PI * 2, Math.PI / 2, Math.PI / 2);
                    const bottomMesh = new THREE.Mesh(bottomGeo, glassMat);
                    bottomMesh.position.y = -2.6;
                    bottomMesh.rotation.x = Math.PI;
                    tubeGroup.add(bottomMesh);

                    // Rubber Cap Top
                    const capGeo = new THREE.CylinderGeometry(1.22, 1.25, 1.3, 32);
                    const capMat = new THREE.MeshStandardMaterial({ color: 0xa855f7, roughness: 0.4, metalness: 0.2 });
                    capMesh = new THREE.Mesh(capGeo, capMat);
                    capMesh.position.y = 3.1;
                    tubeGroup.add(capMesh);

                    // Liquid inside tube
                    const liquidGeo = new THREE.CylinderGeometry(1.02, 0.98, 3.2, 32);
                    const liquidMat = new THREE.MeshStandardMaterial({ color: 0x881337, roughness: 0.2, transparent: true, opacity: 0.85 });
                    liquidMesh = new THREE.Mesh(liquidGeo, liquidMat);
                    liquidMesh.position.y = -0.9;
                    tubeGroup.add(liquidMesh);

                    // SST Gel Disk
                    const gelGeo = new THREE.CylinderGeometry(1.03, 1.01, 0.5, 32);
                    const gelMat = new THREE.MeshStandardMaterial({ color: 0xfacc15, roughness: 0.2, transparent: true, opacity: 0.9 });
                    gelMesh = new THREE.Mesh(gelGeo, gelMat);
                    gelMesh.position.y = -1.8;
                    gelMesh.visible = false;
                    tubeGroup.add(gelMesh);
                }

                function update3DTube(key) {
                    const data = tubesData[key];
                    if (!data) return;

                    capMesh.material.color.setHex(data.capColor);
                    liquidMesh.material.color.setHex(data.liquidColor);

                    if (data.hasGel) {
                        gelMesh.visible = true;
                    } else {
                        gelMesh.visible = false;
                    }

                    document.getElementById('tube-badge').innerText = data.name;
                    document.getElementById('order-val').innerText = data.order;
                    document.getElementById('additive-val').innerText = data.additive;
                    document.getElementById('inversion-val').innerText = data.inversions;
                    document.getElementById('mechanism-val').innerText = data.mechanism;

                    const testsHtml = data.tests.map(t => `<span class="test-tag">${'$'}{t}</span>`).join('');
                    document.getElementById('tests-list').innerHTML = testsHtml;
                }

                function animate() {
                    requestAnimationFrame(animate);
                    if (!isDragging) {
                        tubeGroup.rotation.y += 0.006;
                    }
                    renderer.render(scene, camera);
                }

                function selectTube(key) {
                    currentTubeKey = key;
                    document.querySelectorAll('.tube-chip').forEach(chip => {
                        if (chip.getAttribute('onclick').includes(key)) chip.classList.add('active');
                        else chip.classList.remove('active');
                    });
                    update3DTube(key);
                }

                window.onload = () => {
                    init3D();
                    selectTube('edta');
                };

                window.onresize = () => {
                    const container = document.getElementById('canvas-container');
                    camera.aspect = container.clientWidth / container.clientHeight;
                    camera.updateProjectionMatrix();
                    renderer.setSize(container.clientWidth, container.clientHeight);
                };
            </script>
        </body>
        </html>
    """.trimIndent()
}
