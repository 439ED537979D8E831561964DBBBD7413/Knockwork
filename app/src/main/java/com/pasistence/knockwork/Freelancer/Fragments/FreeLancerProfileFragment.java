package com.pasistence.knockwork.Freelancer.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.pasistence.knockwork.R;

import info.hoang8f.widget.FButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FreeLancerProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FreeLancerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeLancerProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public FButton btnSubmit;
    public EditText edtTitle,edtMinhour;
    public Spinner spnAvability;
    public TextView txtContactNo,txtDateofBirth;
    RadioButton rdMale,rdFemale;
    RadioGroup rdgGender;
    ImageView imgCorrect,imgEdit;

    private OnFragmentInteractionListener mListener;

    public FreeLancerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreeLancerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeLancerProfileFragment newInstance(String param1, String param2) {
        FreeLancerProfileFragment fragment = new FreeLancerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_free_lancer_profile, container, false);
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_free_lancer_profile, container, false);

        mInit(view);
        return view;
    }

    private void mInit(View view) {
        btnSubmit =(FButton)view.findViewById(R.id.freelancer_profile_btnSubmit) ;

        edtMinhour = (EditText)view.findViewById(R.id.freelancer_profile_edtrate);
        edtTitle = (EditText)view.findViewById(R.id.freelancer_profile_edittitle);

        spnAvability = (Spinner)view.findViewById(R.id.freelancer_profile_spn_avalibility);
        txtContactNo = (TextView)view.findViewById(R.id.freelancer_profile_MNumber);

        imgCorrect=(ImageView)view.findViewById(R.id.freelancer_profile_imgright);
        imgEdit=(ImageView)view.findViewById(R.id.freelancer_profile_imgedit);

        txtDateofBirth = (TextView)view.findViewById(R.id.freelancer_profile_DOB);
        rdFemale =(RadioButton)view.findViewById(R.id.freelancer_profile_radiomale);
        rdMale=(RadioButton)view.findViewById(R.id.freelancer_profile_radioFemale) ;
        rdgGender =(RadioGroup)view.findViewById(R.id.rdg_group);

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
