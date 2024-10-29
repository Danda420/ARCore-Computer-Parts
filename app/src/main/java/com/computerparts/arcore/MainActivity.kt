package com.computerparts.arcore

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var sceneView: ArSceneView
    private lateinit var placeButton: Button
    private lateinit var modelDropdown: Spinner
    private var selectedModelPath: String = "pc_case.glb" // Default model path

    private val models = mapOf(
        "Case" to "pc_case.glb",
        "PSU" to "psu.glb",
        "GPU" to "gpu.glb",
        "Fan" to "fan.glb",
        "Monitor" to "monitor.glb",
        "Motherboard" to "mobo.glb"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.sceneView)

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            this.lightEstimationMode = io.github.sceneview.ar.arcore.LightEstimationMode.DISABLED
        }

        modelDropdown = findViewById(R.id.modelDropdown)
        placeButton = findViewById(R.id.place)

        val modelList = models.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modelList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modelDropdown.adapter = adapter
        modelDropdown.onItemSelectedListener = this  // Set the listener here

        placeButton.setOnClickListener {
            placeModel()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        selectedModelPath = models[parent.getItemAtPosition(position)]!!
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Optionally handle the case when nothing is selected
    }

    private fun placeModel() {
        // Create a new model node each time to allow multiple placements
        val newModelNode = ArModelNode(placementMode = PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = selectedModelPath,
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) {
                sceneView.planeRenderer.isVisible = true
            }
        }

        sceneView.addChild(newModelNode)
        newModelNode.anchor()
    }

    override fun onDestroy() {
        super.onDestroy()
        sceneView.children.forEach { it.destroy() }
    }
}
