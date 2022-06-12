package com.example.earningapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.earningapp.Model.ProfileModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    ActivityProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference reference;

    private static final int IMAGE_PICKER=1;
    private Uri photoUri;
    private String imageUrl;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("users");


        progressDialog=new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCancelable(false);

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            ProfileModel model=snapshot.getValue(ProfileModel.class);

                            binding.name.setText(model.getName());
                            binding.email.setText(model.getEmail());
                            binding.coins.setText(model.getCoins()+"");

                            Glide.with(getApplicationContext())
                                    .load(model.getProfileImg())
                                    .placeholder(R.drawable.profile)
                                    .into(binding.profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ProfileActivity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                finish();
                Toast.makeText(ProfileActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });


        binding.shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String shareBody="Check out the best earning app . Download it"+getString(R.string.app_name)+
                       " from Play store\n "+
                       "https://play.google.com/store/apps/details?id="+
                       getPackageName();

               Intent intent=new Intent(Intent.ACTION_SEND);
               intent.putExtra(Intent.EXTRA_TEXT,shareBody);
               intent.setType("text/plain");
               startActivity(intent);
            }
        });


        binding.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_PICKER);

              /*  Dexter.withContext(getApplicationContext())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted())
                                {
                                    Intent intent=new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent,IMAGE_PICKER);
                                    
                                }else {
                                    Toast.makeText(ProfileActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();
                */
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImg();
               //progressDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_PICKER && resultCode==RESULT_OK)
        {
            if (data!=null)
            {

                photoUri=data.getData();
               // imageUrl=photoUri.toString();

                binding.update.setVisibility(View.VISIBLE);
            }
        }
    }

    public void uploadImg()
    {

        if (photoUri==null)
        {
            return;
        }

        String fileName=user.getUid()+".jpg";

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReference().child("Images/"+fileName);

       // UploadTask uploadTask=storageReference.putFile(photoUri);



        storageReference.putFile(photoUri)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       storageReference.getDownloadUrl()
                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {

                                       imageUrl=uri.toString();
                                       uploadImageUriToDatabase();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {

                           }
                       });


                   }
               }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                long totalSize=snapshot.getTotalByteCount();
                long transferSize=snapshot.getBytesTransferred();

               // progressDialog.setMessage("Uploaded"+(int)transferSize+" / "+(int)totalSize +" ");
                progressDialog.setMessage("uploaded");
            }
        });


    }
    public void uploadImageUriToDatabase()
    {

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("profileImg",imageUrl);

        reference.child(user.getUid())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        binding.update.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}