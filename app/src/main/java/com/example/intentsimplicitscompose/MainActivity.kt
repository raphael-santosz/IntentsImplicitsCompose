package com.example.intentsimplicitscompose

import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.intentsimplicitscompose.ui.theme.IntentsImplicitsComposeTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState



class MainActivity : ComponentActivity() {

    private var callPhonePerm = mutableStateOf(false)

    private val permissionCallPhone = android.Manifest.permission.CALL_PHONE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntentsImplicitsComposeTheme {
                MyApp(modifier = Modifier)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            callPhonePerm.value = true
        }
    }


    @Composable
    fun MyApp(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val launchCallPhonePerm =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    callPhonePerm.value = true
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    callPhonePerm.value = false
                }
            }

        LaunchedEffect(Unit) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                callPhonePerm.value = true
            } else {
                Log.i(TAG, "Requesting permission")
                launchCallPhonePerm.launch(permissionCallPhone)
            }
        }

        Surface(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // 🔥 Scroll agora envolve toda a tela
            ) {
                Text(
                    modifier = modifier.padding(vertical = 28.dp),
                    text = stringResource(R.string.bienvenidos)
                )

                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally).padding(vertical = 10.dp),
                    text = stringResource(R.string.ubicando)
                )

                ElevatedButton(
                    modifier = modifier.padding(vertical = 10.dp).align(Alignment.CenterHorizontally),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Seleccionado Localizacion por coordenadas",
                            Toast.LENGTH_LONG
                        ).show()
                        val geo = "geo:$lat,$lon?q=$lat,$lon"
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(geo)
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = stringResource(R.string.Boton1))
                }

                ElevatedButton(
                    modifier = modifier.padding(vertical = 10.dp).align(Alignment.CenterHorizontally),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Seleccionado Localizacion por dirección",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address")
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = stringResource(R.string.Boton2))
                }

                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally).padding(vertical = 30.dp),
                    text = stringResource(R.string.navegando)
                )

                ElevatedButton(
                    modifier = modifier.align(Alignment.CenterHorizontally).padding(vertical = 3.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Accediendo a la web",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW, Uri.parse(webEPS)
                            )
                        context.startActivity(intent)
                    })
                {
                    Text(text = stringResource(R.string.Boton3))
                }

                ElevatedButton(
                    modifier = modifier.align(Alignment.CenterHorizontally).padding(vertical = 10.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Buscando en Google",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(Intent.ACTION_WEB_SEARCH)
                        intent.putExtra(SearchManager.QUERY, textoABuscar)
                        context.startActivity(intent)
                    })
                {
                    Text(text = stringResource(R.string.Boton4))
                }

                Text(
                    modifier = modifier.padding(vertical = 30.dp),
                    text = stringResource(R.string.contactando)
                )

                ElevatedButton(
                    modifier = modifier.align(Alignment.CenterHorizontally).padding(vertical = 1.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    enabled = callPhonePerm.value,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Marcando Tlfn. Consergeria",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:" + telef)
                        )
                        context.startActivity(intent)
                    })
                {
                    Text(text = stringResource(R.string.Boton5))
                }

                ElevatedButton(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 10.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Enviando SMS",
                            Toast.LENGTH_LONG
                        ).show()
                        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:$telefSMS")
                            putExtra("sms_body", textoSMS)
                        }
                        context.startActivity(smsIntent)
                    }
                ) {
                    Text(text = "Enviar SMS")
                }

                ElevatedButton(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 10.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Accediendo a la agenda de contactos",
                            Toast.LENGTH_LONG
                        ).show()
                        val contactsIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("content://contacts/people/")
                        }
                        context.startActivity(contactsIntent)
                    }
                ) {
                    Text(text = "Abrir Agenda de Contactos")
                }

                GalleryImagePicker(modifier)
                CameraButton(modifier)
            }
        }
    }

    @Composable
    fun CameraButton(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val cameraPermissionGranted = remember { mutableStateOf(false) }

        val lifecycleOwner = LocalLifecycleOwner.current
        val currentContext by rememberUpdatedState(context)

        DisposableEffect(lifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    cameraPermissionGranted.value = ContextCompat.checkSelfPermission(
                        currentContext,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // Primeiro exibe o botão
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                modifier = Modifier.padding(vertical = 10.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                onClick = {
                    if (cameraPermissionGranted.value) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        context.startActivity(cameraIntent)
                    } else {
                        Toast.makeText(
                            context,
                            "Permissão não concedida. Vá até as configurações e conceda manualmente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                enabled = cameraPermissionGranted.value
            ) {
                Text(text = "Abrir Câmera")
            }
        }

        // Exibe a mensagem apenas se a permissão não foi concedida
        if (!cameraPermissionGranted.value) {
            Text(
                text = "Para usar a câmera, conceda a permissão manualmente nas configurações do aplicativo.",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    @Composable
    fun GalleryImagePicker(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri.value = uri
            if (uri != null) {
                Toast.makeText(context, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Selección cancelada", Toast.LENGTH_SHORT).show()
            }
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                modifier = modifier.padding(end = 10.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                onClick = { launcher.launch("image/*") }
            ) {
                Text(text = "Abrir Galería")
            }

            selectedImageUri.value?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }

    companion object {
        const val lat = "41.60788"
        const val lon = "0.623333"
        const val address = "Carrer de Jaume II, 69, Lleida"
        const val webEPS = "http://www.eps.udl.cat/"
        const val textoABuscar = "escola politecnica superior UdL"
        const val telef = "666666666"
        const val telefSMS = "123456789"
        const val textoSMS = "Hola, este es un mensaje de prueba enviado desde la app."

    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        IntentsImplicitsComposeTheme {
            MyApp()
        }
    }
}
