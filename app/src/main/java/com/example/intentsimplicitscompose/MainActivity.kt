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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.intentsimplicitscompose.ui.theme.IntentsImplicitsComposeTheme

class MainActivity : ComponentActivity() {

    private var callPhonePerm = mutableStateOf(false)

    private val permissionCall = arrayOf(
        android.Manifest.permission.CALL_PHONE,
    )

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
        if (permissionCall.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            callPhonePerm.value = true
        }
    }


    @Composable
    fun MyApp(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val launchCallPerm =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissionMaps ->
                val areGranted = permissionMaps.values.reduce { acc, next -> acc && next }
                if (areGranted) {
                    Toast.makeText(context,"Permission Granted", Toast.LENGTH_SHORT).show()
                    callPhonePerm.value = true
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    callPhonePerm.value = false
                }
            }

        LaunchedEffect(Unit) {
            if (!permissionCall.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                Log.i(TAG,"Requesting permission")
                launchCallPerm.launch(permissionCall)
            }
            else {callPhonePerm.value = true}
        }

        Surface(modifier = modifier)
        {
            Column(modifier.fillMaxSize()) {
                Text(
                    modifier = modifier.padding(vertical = 28.dp),
                    text = stringResource(R.string.bienvenidos)
                )
                Text(
                    modifier = modifier.align(alignment = Alignment.CenterHorizontally).padding(vertical = 10.dp),
                    text = stringResource(R.string.ubicando)
                )
                ElevatedButton(
                    modifier = modifier.padding(vertical = 10.dp).align(alignment = Alignment.CenterHorizontally),
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
                )
                {
                    Text(
                        text = stringResource(R.string.Boton1)
                    )
                }

                ElevatedButton(
                    modifier = modifier.padding(vertical = 10.dp).align(alignment = Alignment.CenterHorizontally),
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
                )
                {
                    Text(text = stringResource(R.string.Boton2))
                }

                Text(
                    modifier = modifier.align(alignment = Alignment.CenterHorizontally).padding(vertical = 30.dp),
                    text = stringResource(R.string.navegando)
                )

                ElevatedButton(
                    modifier = modifier.align(alignment = Alignment.CenterHorizontally).padding(vertical = 3.dp),
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
                    modifier = modifier.align(alignment = Alignment.CenterHorizontally).padding(vertical = 10.dp),
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
                    modifier = modifier.align(alignment = Alignment.CenterHorizontally).padding(vertical = 1.dp),
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
                            Uri.parse("tel:" + "666666666")
                        )
                        context.startActivity(intent)
                    })
                {
                    Text(text = stringResource(R.string.Boton5))
                }

                ElevatedButton(
                    modifier = modifier.padding(vertical = 30.dp).padding(vertical = 4.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Accediendo a contactos",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = ContactsContract.Contacts.CONTENT_URI
                        context.startActivity(intent)
                    })
                {
                    Text(text = stringResource(R.string.Boton6))
                }
            }
        }
    }

    companion object {
        const val lat = "41.60788"
        const val lon = "0.623333"
        const val address = "Carrer de Jaume II, 69, Lleida"
        const val webEPS = "http://www.eps.udl.cat/"
        const val textoABuscar = "escola politecnica superior UdL"
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        IntentsImplicitsComposeTheme {
            MyApp()
        }
    }
}