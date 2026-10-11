package app.feeltheone.simple;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

/* La app entera es una ventana que muestra la página del contador.

   La página va dentro de la app y se sirve con una dirección https falsa
   (appassets.androidplatform.net). Hace falta porque el navegador solo deja
   usar el micrófono en páginas seguras; abrirla como archivo suelto no vale. */
public class MainActivity extends Activity {
    private static final int PEDIDO_MICRO = 1;
    private WebView web;
    private PermissionRequest pendiente;

    @Override
    protected void onCreate(Bundle guardado) {
        super.onCreate(guardado);
        // Que la pantalla no se apague mientras se baila
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        setContentView(web);

        WebSettings ajustes = web.getSettings();
        ajustes.setJavaScriptEnabled(true);
        ajustes.setDomStorageEnabled(true);
        ajustes.setMediaPlaybackRequiresUserGesture(false);

        final WebViewAssetLoader cargador = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                return cargador.shouldInterceptRequest(r.getUrl());
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            /* La página pide el micrófono. Se le da solo eso, y solo si
               Android ya dio permiso; si no, se pregunta primero. */
            @Override
            public void onPermissionRequest(final PermissionRequest pedido) {
                runOnUiThread(() -> {
                    boolean quiereAudio = false;
                    for (String r : pedido.getResources()) {
                        if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r)) quiereAudio = true;
                    }
                    if (!quiereAudio) { pedido.deny(); return; }
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) {
                        dar(pedido);
                    } else {
                        pendiente = pedido;
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PEDIDO_MICRO);
                    }
                });
            }
        });

        web.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    private void dar(PermissionRequest pedido) {
        pedido.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
    }

    @Override
    public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        if (codigo != PEDIDO_MICRO || pendiente == null) return;
        if (resultados.length > 0 && resultados[0] == PackageManager.PERMISSION_GRANTED) dar(pendiente);
        else pendiente.deny();
        pendiente = null;
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
