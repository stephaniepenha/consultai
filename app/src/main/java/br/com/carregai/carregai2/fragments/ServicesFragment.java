package br.com.carregai.carregai2.fragments;


import br.com.carregai.carregai2.R;
import br.com.carregai.carregai2.adapter.DashboardGridViewAdapter;
import br.com.carregai.carregai2.model.DashboardItem;
import br.com.carregai.carregai2.utils.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renan.boni on 09/08/2017.
 */

public class ServicesFragment extends Fragment {

    private GridView mGridView;
    private DashboardGridViewAdapter mAdapter;
    private List<DashboardItem> mItens;

    public static final int GASTO_DIARIO = 0;
    public static final int VALOR_RECARGA = 1;
    public static final int DIAS_DA_SEMANA = 2;

    private TextView mDisplay;

    public ServicesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getItensList();
        View view = inflater.inflate(R.layout.service_layout, container, false);
        mDisplay = view.findViewById(R.id.tv_display);
        mGridView = view.findViewById(R.id.dashboard_gridview);
        mGridView.setAdapter(new DashboardGridViewAdapter(view.getContext(), 1, mItens));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case GASTO_DIARIO:
                        valorDiario();
                        break;
                    case VALOR_RECARGA:
                        valorRecarga();
                        break;
                    case DIAS_DA_SEMANA:
                        selectDays();
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateViews();
    }

    private void getItensList() {
        mItens = new ArrayList<>();

        mItens.add(new DashboardItem(R.drawable.ic_gasto_diario, "Gasto diário"));
        mItens.add(new DashboardItem(R.drawable.ic_valor_recarga, "Valor de recarga"));
        mItens.add(new DashboardItem(R.drawable.ic_calendar, "Dias de uso"));
        mItens.add(new DashboardItem(R.drawable.ic_ajuda, "Como usar"));
        mItens.add(new DashboardItem(R.drawable.ic_limpar, "Limpar dados"));
        mItens.add(new DashboardItem(R.drawable.ic_viagem_extra, "Viagem extra"));
        mItens.add(new DashboardItem(R.drawable.crop_image_menu_crop, "Title " + 6));
        mItens.add(new DashboardItem(R.drawable.crop_image_menu_crop, "Title " + 6));
    }

    private void updateViews(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mDisplay.setText(Utility.formatValue(sp.getFloat("saldo_atual", 0)));
    }
    public void valorDiario() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Valor Diário");

        final CurrencyEditText input = new CurrencyEditText(getActivity(), null);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final float saldo = sp.getFloat("valor_diario", 0);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Digite o valor diário...");
        input.setText(Utility.formatValue(saldo));

        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float db = Utility.stringToFloat(input.getText().toString());

                storeValue("valor_diario", db);

                Utility.makeText(getActivity(), "O seu gasto diário foi salvo.");
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void valorRecarga() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Valor Recarga");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final float saldo = sp.getFloat("saldo_atual", 0);

        final CurrencyEditText input = new CurrencyEditText(getActivity(), null);
        input.setText(Utility.formatValue(sp.getFloat("valor_recarga", 0)));
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float valorRecarga = Utility.stringToFloat(input.getText().toString());
                float saldoFinal = saldo + valorRecarga;

                mDisplay.setText(Utility.formatValue(saldoFinal));

                storeValue("saldo_atual", saldoFinal);
                storeValue("valor_recarga", valorRecarga);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void selectDays() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogWeek dialogWeek = new DialogWeek();
        dialogWeek.show(fragmentManager, "dias_semana");
    }

    private void storeValue(String key, float value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putFloat(key, value);

        editor.commit();
    }
}