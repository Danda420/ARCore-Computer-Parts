package com.computerparts.arcore

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var sceneView: ArSceneView
    private lateinit var selectButton: Button
    private lateinit var delButton: Button
    private lateinit var modelDropdown: Spinner
    private val placedModels = mutableListOf<ArModelNode>()
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
            this.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        }

        modelDropdown = findViewById(R.id.modelDropdown)
        selectButton = findViewById(R.id.select)
        delButton = findViewById(R.id.delmodels)


        val modelList = models.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modelList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modelDropdown.adapter = adapter
        modelDropdown.onItemSelectedListener = this  // Set the listener here

        selectButton.setOnClickListener {
            placeModel()
        }
        delButton.setOnClickListener {
            placedModels.forEach { modelNode ->
                modelNode.destroy()  // Destroy the model node
            }
            placedModels.clear()
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
        val newModelNode = ArModelNode(sceneView.engine,PlacementMode.INSTANT).apply {
            // Load the model asynchronously
            loadModelGlbAsync(
                glbFileLocation = selectedModelPath,
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) { error ->
                if (error != null) {
                    // Handle loading error if needed
                } else {
                    // Successfully loaded, anchor the model
                    anchor()
                }
            }
        }

        // Add the model node to the scene
        sceneView.addChild(newModelNode)
        placedModels.add(newModelNode)
    }


    override fun onDestroy() {
        super.onDestroy()
        sceneView.children.forEach { it.destroy() }
    }
}