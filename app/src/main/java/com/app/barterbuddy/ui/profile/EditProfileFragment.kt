package com.app.barterbuddy.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.databinding.FragmentEditProfileBinding
import com.app.barterbuddy.di.models.UpdateUser
import com.app.barterbuddy.ui.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileFragment : Fragment() {

    private var file: File? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentEditProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        lifecycleScope.launch {
            profileViewModel.getDetailUser(firebaseUser!!.uid).observe(viewLifecycleOwner){
                if (it != null){
                    binding.username.setText(it.username)
                    binding.email.setText(it.email)
                    binding.city.setText(it.city)
                } else {
                    Log.d("error fetching", "error")
                }
            }
        }

        binding.cancel.setOnClickListener {
            (activity as MainActivity).loadFragments(ProfileFragment())
        }

        binding.image.setOnClickListener {
            val intent = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            resultLauncherGallery.launch(intent)
        }

        binding.save.setOnClickListener {
           val dataUsername = binding.username.text.toString()
           val dataEmail = binding.email.text.toString()
           val dataCity = binding.city.text.toString()

            when {
                dataUsername.isEmpty() -> messageToast("Username masih kosong")
                dataEmail.isEmpty() -> messageToast("Email masih kosong")
                dataCity.isEmpty() -> messageToast("Kota masih kosong")
                file == null -> messageToast("Foto Profile Masih kosong")
                file!!.length() > 1_048_576 -> messageToast("Ukuran Foto tidak boleh lebih dari 1 MB")
                else -> {
                    val poto = file!!.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    val request = UpdateUser(
                        userId = firebaseUser!!.uid,
                        username = dataUsername.toRequestBody("text/plain".toMediaType()),
                        email = dataEmail.toRequestBody("text/plain".toMediaType()),
                        city = dataCity.toRequestBody("text/plain".toMediaType()),
                        image = MultipartBody.Part.createFormData("profile_img", file!!.name, poto)
                    )
                    lifecycleScope.launch {
                        profileViewModel.updateUser(request).observe(viewLifecycleOwner){
                            if (it != null){
                                messageToast("Profile Berhasil Di Update")
                                (activity as MainActivity).loadFragments(ProfileFragment())
                            } else {
                                messageToast("Profile Gagal Di Update")
                            }
                        }
                    }
                }

            }
        }

        return binding.root
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null){
            val path = getPath(requireContext(), uri)
            file = File(path!!)
        }
    }

    private fun getPath(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, data, null, null, null)
        cursor?.use {
            val indexColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(indexColumn)
        }
        return realPath
    }

    private fun messageToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

}