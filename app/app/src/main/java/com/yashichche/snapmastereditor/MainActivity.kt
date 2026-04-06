package com.yashichche.snapmastereditor

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    var bitmap: Bitmap? = null

    val PICK = 1
    val CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.photoView)

        findViewById<Button>(R.id.galleryBtn).setOnClickListener {

            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i,PICK)
        }

        findViewById<Button>(R.id.cameraBtn).setOnClickListener {

            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i,CAMERA)
        }

        findViewById<Button>(R.id.filterBtn).setOnClickListener {

            bitmap?.let {

                val matrix = ColorMatrix(
                    floatArrayOf(
                        1.2f,0f,0f,0f,0f,
                        0f,1.1f,0f,0f,0f,
                        0f,0f,1.1f,0f,0f,
                        0f,0f,0f,1f,0f
                    )
                )

                val bmp = Bitmap.createBitmap(it.width,it.height,it.config)
                val canvas = Canvas(bmp)
                val paint = Paint()
                paint.colorFilter = ColorMatrixColorFilter(matrix)

                canvas.drawBitmap(it,0f,0f,paint)

                bitmap = bmp
                imageView.setImageBitmap(bmp)
            }
        }

        findViewById<Button>(R.id.blurBtn).setOnClickListener {

            bitmap?.let {

                val small = Bitmap.createScaledBitmap(it,it.width/8,it.height/8,false)
                val blurred = Bitmap.createScaledBitmap(small,it.width,it.height,false)

                bitmap = blurred
                imageView.setImageBitmap(blurred)
            }
        }

        findViewById<Button>(R.id.saveBtn).setOnClickListener {

            bitmap?.let {

                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    it,
                    "SnapMaster",
                    "Edited with SnapMaster Editor"
                )

                Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(req:Int,res:Int,data:Intent?) {

        super.onActivityResult(req,res,data)

        if(res==Activity.RESULT_OK){

            if(req==PICK){

                val uri: Uri? = data?.data
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
                imageView.setImageBitmap(bitmap)
            }

            if(req==CAMERA){

                val photo = data?.extras?.get("data") as Bitmap
                bitmap = photo
                imageView.setImageBitmap(photo)
            }
        }
    }
}
