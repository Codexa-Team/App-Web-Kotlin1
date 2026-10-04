package com.example.spacepulse.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BuscarView() {
    val darkBlue = Color(0xFF064B78)
    val lightBackground = Color(0xFFF4F4F4)
    val borderGray = Color(0xFFE0E0E0)

    var vehicleName by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var minimumPrice by remember { mutableStateOf("") }
    var maximumPrice by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text = "Buscar Vehículos",
            color = darkBlue,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehicleName,
                onValueChange = { vehicleName = it },
                placeholder = { Text("Nombre del vehículo", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar por nombre",
                        tint = darkBlue
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = searchFieldColors(borderGray, darkBlue, lightBackground)
            )

            Button(
                onClick = { vehicleName = vehicleName.trim() },
                shape = RoundedCornerShape(10.dp),
                contentPadding = ButtonDefaults.ContentPadding,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB7ED)),
                modifier = Modifier.height(56.dp)
            ) {
                Text(
                    text = "Buscar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Filtrar por marca:",
            color = darkBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        FilterInput(
            value = brand,
            onValueChange = { brand = it },
            placeholder = "Ej. Toyota",
            onAdd = { brand = brand.trim() },
            darkBlue = darkBlue,
            borderGray = borderGray,
            background = lightBackground
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Filtrar por rango de precios:",
            color = darkBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minimumPrice,
                onValueChange = { minimumPrice = it },
                placeholder = { Text("Precio mínimo", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = searchFieldColors(borderGray, darkBlue, lightBackground)
            )

            OutlinedTextField(
                value = maximumPrice,
                onValueChange = { maximumPrice = it },
                placeholder = { Text("Precio máximo", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = searchFieldColors(borderGray, darkBlue, lightBackground)
            )

            IconButton(
                onClick = {
                    minimumPrice = minimumPrice.trim()
                    maximumPrice = maximumPrice.trim()
                },
                modifier = Modifier
                    .size(52.dp)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar rango de precios",
                    tint = darkBlue,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onAdd: () -> Unit,
    darkBlue: Color,
    borderGray: Color,
    background: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f),
            colors = searchFieldColors(borderGray, darkBlue, background)
        )

        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(52.dp)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Agregar marca",
                tint = darkBlue,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun searchFieldColors(
    borderGray: Color,
    darkBlue: Color,
    background: Color
) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = darkBlue,
    unfocusedBorderColor = borderGray,
    focusedContainerColor = background,
    unfocusedContainerColor = background
)
