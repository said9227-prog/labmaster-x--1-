package com.example.ui.screens

import android.annotation.SuppressLint
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
fun ThreeJsBloodTypesScreen(
    onBack: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isWebGlError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أطلس فصائل الدم 3D (Three.js)", fontWeight = FontWeight.Bold) },
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
                    .testTag("threejs_blood_types_webview"),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        // Enable hardware acceleration for WebGL 3D rendering
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
                            generateBloodTypesThreeJsHtml(),
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
                        Text("عذراً، حدث تعذر في محرك الرسم ثلاثي الأبعاد 3D", fontWeight = FontWeight.Bold)
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

private fun generateBloodTypesThreeJsHtml(): String {
    return """
        <!DOCTYPE html>
        <html lang="ar" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <title>3D Blood Types Visualizer</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
                body {
                    background-color: #0d1117;
                    color: #f0f6fc;
                    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    overflow-x: hidden;
                    touch-action: manipulation;
                }
                #canvas-container {
                    width: 100vw;
                    height: 52vh;
                    position: relative;
                    background: radial-gradient(circle at center, #1a233a 0%, #0d1117 100%);
                }
                #canvas-container canvas { width: 100%; height: 100%; display: block; }
                
                .overlay-3d {
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    background: rgba(13, 17, 23, 0.85);
                    padding: 8px 14px;
                    border-radius: 20px;
                    font-size: 12px;
                    border: 1px solid rgba(255,255,255,0.1);
                    backdrop-filter: blur(8px);
                }

                .controls-panel {
                    padding: 16px;
                    background: #161b22;
                    border-top: 1px solid #30363d;
                    min-height: 48vh;
                }
                .section-title {
                    font-size: 15px;
                    font-weight: bold;
                    margin-bottom: 10px;
                    color: #58a6ff;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }
                .type-grid {
                    display: grid;
                    grid-template-columns: repeat(4, 1fr);
                    gap: 8px;
                    margin-bottom: 16px;
                }
                .type-btn {
                    background: #21262d;
                    border: 1px solid #30363d;
                    color: #c9d1d9;
                    padding: 10px 4px;
                    border-radius: 10px;
                    font-size: 15px;
                    font-weight: bold;
                    cursor: pointer;
                    text-align: center;
                    transition: all 0.2s ease;
                }
                .type-btn.active {
                    background: #238636;
                    border-color: #2ea043;
                    color: #ffffff;
                    box-shadow: 0 0 12px rgba(46, 160, 67, 0.4);
                    transform: scale(1.03);
                }
                .matrix-card {
                    background: #0d1117;
                    border: 1px solid #30363d;
                    border-radius: 12px;
                    padding: 14px;
                    margin-bottom: 12px;
                }
                .matrix-row {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 8px;
                    font-size: 13px;
                }
                .badge-list {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 6px;
                }
                .badge {
                    padding: 4px 10px;
                    border-radius: 14px;
                    font-size: 12px;
                    font-weight: bold;
                }
                .badge-give { background: rgba(46, 160, 67, 0.2); color: #3fb950; border: 1px solid rgba(46, 160, 67, 0.4); }
                .badge-receive { background: rgba(56, 139, 253, 0.2); color: #58a6ff; border: 1px solid rgba(56, 139, 253, 0.4); }
                
                .info-box {
                    font-size: 12px;
                    color: #8b949e;
                    line-height: 1.6;
                    background: rgba(255,255,255,0.03);
                    padding: 10px;
                    border-radius: 8px;
                    margin-top: 10px;
                }
                .antigen-legend {
                    display: flex;
                    gap: 12px;
                    font-size: 11px;
                    margin-top: 6px;
                }
                .legend-item { display: flex; align-items: center; gap: 4px; }
                .dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
            </style>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
        </head>
        <body>
            <div id="canvas-container">
                <div class="overlay-3d" id="cell-label">فصيلة الدم 3D: A+</div>
            </div>

            <div class="controls-panel">
                <div class="section-title">اختر فصيلة الدم لاستعراض النموذج 3D والتوافق:</div>
                <div class="type-grid">
                    <button class="type-btn" onclick="selectBloodType('O-')">O-</button>
                    <button class="type-btn" onclick="selectBloodType('O+')">O+</button>
                    <button class="type-btn" onclick="selectBloodType('A-')">A-</button>
                    <button class="type-btn active" onclick="selectBloodType('A+')">A+</button>
                    <button class="type-btn" onclick="selectBloodType('B-')">B-</button>
                    <button class="type-btn" onclick="selectBloodType('B+')">B+</button>
                    <button class="type-btn" onclick="selectBloodType('AB-')">AB-</button>
                    <button class="type-btn" onclick="selectBloodType('AB+')">AB+</button>
                </div>

                <div class="matrix-card">
                    <div class="section-title" style="font-size: 14px; margin-bottom: 6px;">مصفوفة التبرع والاستقبال الآمن:</div>
                    <div class="matrix-row">
                        <span>🩸 يمكنه التبرع بـ (Give to):</span>
                        <div class="badge-list" id="give-list"></div>
                    </div>
                    <div style="height: 8px;"></div>
                    <div class="matrix-row">
                        <span>💉 يمكنه استقبال الدم من (Receive from):</span>
                        <div class="badge-list" id="receive-list"></div>
                    </div>
                </div>

                <div class="matrix-card">
                    <div class="section-title" style="font-size: 13px;">الخصائص الجينية ومستضدات الغشاء (Antigens & Antibodies):</div>
                    <div id="type-details" class="info-box"></div>
                    <div class="antigen-legend">
                        <div class="legend-item"><span class="dot" style="background: #ef4444;"></span> مستضد A (Red Pyramid)</div>
                        <div class="legend-item"><span class="dot" style="background: #3b82f6;"></span> مستضد B (Blue Sphere)</div>
                        <div class="legend-item"><span class="dot" style="background: #22c55e;"></span> عامل Rh(+) (Green Cube)</div>
                    </div>
                </div>
            </div>

            <script>
                // Blood Types Compatibility Data
                const bloodData = {
                    'O-': {
                        give: ['O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'],
                        receive: ['O-'],
                        antigens: { A: false, B: false, Rh: false },
                        antibodies: 'Anti-A & Anti-B',
                        desc: 'متبرع عام لكريات الدم الحمراء (Universal RBC Donor). لا يحمل مستضدات A أو B أو Rh على غشائه.',
                        prevalence: 'نادر (~7%)'
                    },
                    'O+': {
                        give: ['O+', 'A+', 'B+', 'AB+'],
                        receive: ['O-', 'O+'],
                        antigens: { A: false, B: false, Rh: true },
                        antibodies: 'Anti-A & Anti-B',
                        desc: 'أكثر الفصائل انتشاراً في العالم (~38%). يحمل عامل Rh الموجب فقط.',
                        prevalence: 'شائع جداً (~38%)'
                    },
                    'A-': {
                        give: ['A-', 'A+', 'AB-', 'AB+'],
                        receive: ['O-', 'A-'],
                        antigens: { A: true, B: false, Rh: false },
                        antibodies: 'Anti-B',
                        desc: 'يحمل مستضدات A فقط، بدون عامل Rh.',
                        prevalence: 'قليل الانتشار (~6%)'
                    },
                    'A+': {
                        give: ['A+', 'AB+'],
                        receive: ['O-', 'O+', 'A-', 'A+'],
                        antigens: { A: true, B: false, Rh: true },
                        antibodies: 'Anti-B',
                        desc: 'ثاني أكثر الفصائل انتشاراً (~34%). يحمل مستضد A وعامل Rh الموجب.',
                        prevalence: 'شائع (~34%)'
                    },
                    'B-': {
                        give: ['B-', 'B+', 'AB-', 'AB+'],
                        receive: ['O-', 'B-'],
                        antigens: { A: false, B: true, Rh: false },
                        antibodies: 'Anti-A',
                        desc: 'يحمل مستضد B فقط على سطح الكرية الحمراء.',
                        prevalence: 'نادر (~2%)'
                    },
                    'B+': {
                        give: ['B+', 'AB+'],
                        receive: ['O-', 'O+', 'B-', 'B+'],
                        antigens: { A: false, B: true, Rh: true },
                        antibodies: 'Anti-A',
                        desc: 'يحمل مستضد B وعامل Rh الموجب.',
                        prevalence: 'متوسط الانتشار (~9%)'
                    },
                    'AB-': {
                        give: ['AB-', 'AB+'],
                        receive: ['O-', 'A-', 'B-', 'AB-'],
                        antigens: { A: true, B: true, Rh: false },
                        antibodies: 'لا يوجد أضداد للـ A أو B (مستقبل بلازما عام)',
                        desc: 'أندر فصيلة دم على الإطلاق (~1%). يحمل مستضدي A و B معاً دون عامل Rh.',
                        prevalence: 'نادر جداً (~1%)'
                    },
                    'AB+': {
                        give: ['AB+'],
                        receive: ['O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'],
                        antigens: { A: true, B: true, Rh: true },
                        antibodies: 'لا يوجد (مستقبل عام لكريات الدم - Universal Recipient)',
                        desc: 'مستقبل عام لكريات الدم الحمراء (Universal RBC Recipient). يمكنه استقبال كافة الفصائل دون تفاعل حاد.',
                        prevalence: 'قليل (~3%)'
                    }
                };

                let scene, camera, renderer, rbcGroup;
                let currentType = 'A+';
                let isDragging = false;
                let previousMousePosition = { x: 0, y: 0 };

                function init3D() {
                    const container = document.getElementById('canvas-container');
                    const width = container.clientWidth;
                    const height = container.clientHeight;

                    scene = new THREE.Scene();
                    camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
                    camera.position.set(0, 0, 7);

                    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
                    renderer.setSize(width, height);
                    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5));
                    container.appendChild(renderer.domElement);

                    // Lights
                    const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
                    scene.add(ambientLight);

                    const dirLight1 = new THREE.DirectionalLight(0xffffff, 0.8);
                    dirLight1.position.set(5, 5, 7);
                    scene.add(dirLight1);

                    const pointLight = new THREE.PointLight(0xe11d48, 1.2, 10);
                    pointLight.position.set(-3, -2, 3);
                    scene.add(pointLight);

                    // Create RBC Group
                    rbcGroup = new THREE.Group();
                    scene.add(rbcGroup);

                    createRBCModel();
                    updateAntigens('A+');

                    // Touch / Mouse Rotation events
                    const dom = renderer.domElement;
                    dom.addEventListener('mousedown', (e) => { isDragging = true; previousMousePosition = { x: e.clientX, y: e.clientY }; });
                    dom.addEventListener('mousemove', (e) => {
                        if (!isDragging) return;
                        const deltaMove = { x: e.clientX - previousMousePosition.x, y: e.clientY - previousMousePosition.y };
                        rbcGroup.rotation.y += deltaMove.x * 0.01;
                        rbcGroup.rotation.x += deltaMove.y * 0.01;
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
                        rbcGroup.rotation.y += deltaMove.x * 0.012;
                        rbcGroup.rotation.x += deltaMove.y * 0.012;
                        previousMousePosition = { x: e.touches[0].clientX, y: e.touches[0].clientY };
                    }, { passive: true });
                    window.addEventListener('touchend', () => { isDragging = false; });

                    animate();
                }

                function createRBCModel() {
                    // Create Biconcave Erythrocyte geometry using squashed torus/sphere
                    const geometry = new THREE.CylinderGeometry(2, 2, 0.6, 32, 16);
                    
                    // Modify vertices to create smooth biconcave center dip
                    const pos = geometry.attributes.position;
                    for (let i = 0; i < pos.count; i++) {
                        let x = pos.getX(i);
                        let y = pos.getY(i);
                        let z = pos.getZ(i);

                        let distFromCenter = Math.sqrt(x * x + z * z);
                        if (distFromCenter < 1.8) {
                            // Concave indentation
                            let factor = 1.0 - Math.pow(distFromCenter / 1.8, 2);
                            if (y > 0) y -= factor * 0.22;
                            else y += factor * 0.22;
                        }
                        pos.setY(i, y);
                    }
                    geometry.computeVertexNormals();

                    const material = new THREE.MeshStandardMaterial({
                        color: 0xcc1122,
                        roughness: 0.3,
                        metalness: 0.1,
                        clearcoat: 0.3
                    });

                    const rbcMesh = new THREE.Mesh(geometry, material);
                    rbcMesh.name = "rbcBody";
                    rbcGroup.add(rbcMesh);
                }

                function updateAntigens(typeKey) {
                    // Clear existing antigens
                    const toRemove = [];
                    rbcGroup.children.forEach(child => {
                        if (child.name !== "rbcBody") toRemove.push(child);
                    });
                    toRemove.forEach(c => rbcGroup.remove(c));

                    const data = bloodData[typeKey];
                    if (!data) return;

                    // Add Antigen Markers on surface
                    const antigenCount = 14;
                    for (let i = 0; i < antigenCount; i++) {
                        const angle = (i / antigenCount) * Math.PI * 2;
                        const radius = 1.85;
                        const x = Math.cos(angle) * radius;
                        const z = Math.sin(angle) * radius;

                        // Antigen A (Pyramid/Cone - Red)
                        if (data.antigens.A) {
                            const coneGeo = new THREE.ConeGeometry(0.18, 0.4, 4);
                            const coneMat = new THREE.MeshStandardMaterial({ color: 0xef4444, roughness: 0.2 });
                            const cone = new THREE.Mesh(coneGeo, coneMat);
                            cone.position.set(x, 0.3, z);
                            cone.rotation.z = -Math.PI / 2;
                            cone.rotation.y = -angle;
                            rbcGroup.add(cone);
                        }

                        // Antigen B (Sphere - Blue)
                        if (data.antigens.B) {
                            const sphereGeo = new THREE.SphereGeometry(0.18, 12, 12);
                            const sphereMat = new THREE.MeshStandardMaterial({ color: 0x3b82f6, roughness: 0.2 });
                            const sphere = new THREE.Mesh(sphereGeo, sphereMat);
                            sphere.position.set(x * 0.9, -0.3, z * 0.9);
                            rbcGroup.add(sphere);
                        }

                        // Rh Antigen (+) (Cube - Green)
                        if (data.antigens.Rh && i % 2 === 0) {
                            const cubeGeo = new THREE.BoxGeometry(0.24, 0.24, 0.24);
                            const cubeMat = new THREE.MeshStandardMaterial({ color: 0x22c55e, roughness: 0.2 });
                            const cube = new THREE.Mesh(cubeGeo, cubeMat);
                            cube.position.set(x * 0.6, 0.32, z * 0.6);
                            rbcGroup.add(cube);
                        }
                    }
                }

                function animate() {
                    requestAnimationFrame(animate);
                    if (!isDragging) {
                        rbcGroup.rotation.y += 0.005;
                    }
                    renderer.render(scene, camera);
                }

                function selectBloodType(type) {
                    currentType = type;
                    document.querySelectorAll('.type-btn').forEach(btn => {
                        if (btn.innerText.trim() === type) btn.classList.add('active');
                        else btn.classList.remove('active');
                    });

                    document.getElementById('cell-label').innerText = 'فصيلة الدم 3D: ' + type;
                    updateAntigens(type);

                    const data = bloodData[type];
                    // Update Give list
                    const giveHtml = data.give.map(t => `<span class="badge badge-give">${'$'}{t}</span>`).join('');
                    document.getElementById('give-list').innerHTML = giveHtml;

                    // Update Receive list
                    const receiveHtml = data.receive.map(t => `<span class="badge badge-receive">${'$'}{t}</span>`).join('');
                    document.getElementById('receive-list').innerHTML = receiveHtml;

                    // Details
                    document.getElementById('type-details').innerHTML = `
                        <b>الأضداد في البلازما:</b> ${'$'}{data.antibodies}<br>
                        <b>نسبة الانتشار:</b> ${'$'}{data.prevalence}<br>
                        <b>ملاحظات طبية:</b> ${'$'}{data.desc}
                    `;
                }

                window.onload = () => {
                    init3D();
                    selectBloodType('A+');
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
