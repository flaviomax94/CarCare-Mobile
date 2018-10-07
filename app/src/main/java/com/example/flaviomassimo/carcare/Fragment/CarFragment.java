package com.example.flaviomassimo.carcare.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.example.flaviomassimo.carcare.Activities.MainMenuActivity;
import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Spinner showMenu;
    private EditText license,make,model,km;
    private Button addCar;
    private Car car=new Car();
    private DatabaseReference mRef;
    String UID;
    FirebaseUser user;
    private static final String[] items = {"Gasoline", "Petrol", "GPL", "Other"};
    public CarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarFragment newInstance(String param1, String param2) {
        CarFragment fragment = new CarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_car, container, false);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");

        showMenu=(Spinner) view.findViewById(R.id.show_dropdown_menu);
        license=(EditText) view.findViewById(R.id.LicensePlate);
        make=(EditText) view.findViewById(R.id.Make);
        model=(EditText) view.findViewById(R.id.Model);
        km=(EditText) view.findViewById(R.id.Km);
        addCar=(Button) view.findViewById(R.id.addButton);
        addCar.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        showMenu.setAdapter(adapter);
        showMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        car.setFUEL_TYPE("Gasoline");
                        break;
                    case 1:
                        car.setFUEL_TYPE("Petrol");
                        break;
                    case 2:
                        car.setFUEL_TYPE("GPL");
                        break;
                    case 3:
                        car.setFUEL_TYPE("Other");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.addButton){ createCar(); }
    }





    private void createCar(){
        if(validateForm()){
            car.setLICENSE_PLATE(license.getText().toString());
            car.setKM(Double.parseDouble(km.getText().toString()));
            if(make.getText()!=null){
                car.setMAKE(make.getText().toString());
            }
            if(model.getText()!=null){
                car.setMODEL(model.getText().toString());
            }
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Make").setValue(car.getMAKE());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Model").setValue(car.getMODEL());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Km").setValue(car.getKM());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Fuel").setValue(car.getFUEL_TYPE());
            System.out.println(car.getKM()+" "+car.getLICENSE_PLATE()+" "+car.getMAKE()+" "+car.getMODEL()+" "+car.getFUEL_TYPE());
            Intent j =new Intent(getActivity(), MainMenuActivity.class); startActivity(j);
        }

    }
    private boolean validateForm() {
        boolean valid = true;

        String plate = license.getText().toString();
        if (TextUtils.isEmpty(plate)) {
            license.setError("Required.");
            valid = false;
        } else {
            license.setError(null);
        }

        String kilometers = km.getText().toString();
        if (TextUtils.isEmpty(kilometers)) {
            km.setError("Required.");
            valid = false;
        } else {
            km.setError(null);
        }

        return valid;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i= view.getId();
        if(i==R.id.show_dropdown_menu){
            switch (position) {
                case 0:
                    car.setFUEL_TYPE("Gasoline");
                    break;
                case 1:
                    car.setFUEL_TYPE("Petrol");
                    break;
                case 2:
                    car.setFUEL_TYPE("GPL");
                    break;
                case 3:
                    car.setFUEL_TYPE("Other");
                    break;
            }


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
