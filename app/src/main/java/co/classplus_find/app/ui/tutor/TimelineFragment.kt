package co.classplus_find.app.ui.tutor

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.TagsAdapter
import co.classplus_find.app.adapters.TutorPostAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.data.models.TutorPostModel
import co.classplus_find.app.databinding.FragmentTimelineBinding
import co.classplus_find.app.util.toast
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.siyamed.shapeimageview.CircularImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.collections.ArrayList

class TimelineFragment: Fragment() {

    lateinit var binding: FragmentTimelineBinding
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var role=""
    var user: FirebaseUser?=null
    var postAdapter: TutorPostAdapter? = null
    var postList: ArrayList<TutorPostModel> = ArrayList()

    var uid:String? = null
    var isUploading=false
    var downloadUrl=""


    var progressContainer: LinearLayout? = null
    var progress: TextView? = null
    var pic: ImageView? = null

    companion object{
        var PARAM_UID  = "PARAM_UID"
        fun newInstance(uid: String? = null) = TimelineFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM_UID,uid)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_timeline, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupUi()
        setupListener()
    }

    fun setupListener(){
        binding.addPost1.setOnClickListener {
            var builder=AlertDialog.Builder(activity)
            var view=layoutInflater.inflate(R.layout.post_dialog,null)
            progressContainer=view.findViewById(R.id.progressContainer)
            progress=view.findViewById(R.id.progress)
            pic=view.findViewById(R.id.imgPreview)

            builder.setView(view)
            var dialog=builder.create()
            dialog.show()

            view.findViewById<Button>(R.id.imgButton).setOnClickListener {
                if (checkPermission() == true) {
                    activity?.let { it1 ->
                        ImagePicker.with(it1)
                            .cropSquare() //Crop image(Optional), Check Customization for more option
                            .compress(500) //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)
                            .start()
                    }
                }
            }
            view.findViewById<Button>(R.id.submit).setOnClickListener {
                var caption=view.findViewById<EditText>(R.id.caption).text.toString()
                if(isUploading){
                    Toast.makeText(
                        activity,
                        "Image is getting uploaded!",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(TextUtils.isEmpty(caption)){
                    Toast.makeText(
                        activity,
                        "Write a caption!",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(downloadUrl==""){
                    Toast.makeText(
                        activity,
                        "Upload an image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    postList.add(TutorPostModel(downloadUrl,caption))
                    ref!!.child("timeline")?.setValue(postList)
                    activity?.toast("Post Created")
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        Log.i("fragment", "req-$requestCode res-$resultCode")
        if (requestCode == 2404 && resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data!!.data
            uploadImage(fileUri.toString())
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                .show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadImage(path: String?) {
        isUploading=true
        val file_uri = Uri.parse(path)
        //        Log.i("pathimg", currentSkuForImage)
        val storagePath =
            "Posts/" + Date().time + ".jpg"
        val storage_ref = FirebaseStorage.getInstance().reference
            .child(storagePath)
        val uploadTask = storage_ref.putFile(file_uri)
        uploadTask.addOnProgressListener { it: UploadTask.TaskSnapshot ->
            val per = 100 * it.bytesTransferred / it.totalByteCount
            progressContainer!!.visibility = View.VISIBLE
            progress!!.text = "$per%"
        }
        uploadTask.addOnCompleteListener { it: Task<UploadTask.TaskSnapshot?>? -> }
        uploadTask.addOnFailureListener { it: Exception? ->
            Toast.makeText(
                activity,
                "Image Uploading Failed!",
                Toast.LENGTH_SHORT
            ).show()
        }
        val urlTask =
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                val ref = FirebaseStorage.getInstance().reference
                    .child(storagePath)
                // Continue with the task to get the download URL
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    downloadUrl= downloadUri.toString()
                    isUploading=false

                    progressContainer!!.visibility = View.GONE
                    pic?.let {
                        activity?.let { it1 ->
                            Glide.with(it1)
                                .load(downloadUri.toString())
                                .placeholder(R.drawable.empty_state)
                                .error(R.drawable.empty_state)
                                .into(it)
                        }
                    }
                } else {
                    // Handle failures
                    // ...
                    isUploading=false
                    progressContainer!!.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "Image Uploading Failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setupData(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        role=if(mPrefs.getInt(PreferenceHelper.PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        if(arguments?.getString(PARAM_UID) != null){
            uid = arguments?.getString(PARAM_UID)
            ref = FirebaseDatabase.getInstance()
                .getReference("users/" +uid +"/"+"teacher")
            binding.addPost1.visibility = View.GONE
        }
        else {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            ref = FirebaseDatabase.getInstance()
                .getReference("users/" +uid+ "/" + role)
        }

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun setupUi(){
        postAdapter = TutorPostAdapter(requireContext(),this, ArrayList())

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
            setHasFixedSize(true)
        }

        setupPosts()
    }

    private fun setupPosts(){

        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("timeline").exists()) {
                        postList.clear()
                        for (snap in snapshot.child("timeline").children){
                            postList.add(
                                TutorPostModel(snap.child("image").value.toString(),
                                snap.child("caption").value.toString()))
                        }
                        if(postList.size > 0){
                            binding.rvPosts.visibility = View.VISIBLE
                            postAdapter?.setList(postList)
                            binding.emptyState.visibility = View.GONE
                        }
                        else{
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvPosts.visibility = View.GONE
                        }
                    }
                }else{
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvPosts.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun checkPermission(): Boolean {
        val storage = activity?.applicationContext?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        val camera = activity?.applicationContext?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            )
        }
        val list = mutableListOf<String>()
        if (camera != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.CAMERA)
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return if (!list.isEmpty()) {
            Log.i("permission", "Called")
            val arr= list.toTypedArray()
//            list.toArray(arr)
            activity?.let { ActivityCompat.requestPermissions(it, arr, 2340) }
            false
        } else {
            true
        }
    }
}