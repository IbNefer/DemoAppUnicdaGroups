package com.example.demounicdagroups.features.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.features.channel.ChannelViewModel

@Composable
fun CreateGroup(
    navController: NavController,
    groupsViewModel: GroupViewModel = hiltViewModel(),
    chatViewModel: ChannelViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Color de fondo del tema
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp) // Aumentamos el padding general
                .verticalScroll(rememberScrollState()), // Permite el scroll en pantallas pequeñas
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Un título más prominente
            Text(
                text = "Crear Nuevo Grupo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Grupo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = "Group Name Icon")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = courseCode,
                onValueChange = { courseCode = it },
                label = { Text("Código / Aula") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Create, contentDescription = "Course Code Icon")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Detalles") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Más alto para más texto
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Info, contentDescription = "Details Icon")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    groupsViewModel.createGroup(name, courseCode, details)
                    chatViewModel.addChannel(courseCode)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Crear Grupo",
                    fontSize = 18.sp
                )
            }
        }
    }
}
