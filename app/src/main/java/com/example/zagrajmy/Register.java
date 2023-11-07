package com.example.zagrajmy;


import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {
/*
    private String email, passwordFirst, passwordSecond;
    private TextInputEditText edytujPoleEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        edytujPoleEmail = findViewById(R.id.email);
        Button przyciskRejestracji = findViewById(R.id.registerButton);
        TextView textView = findViewById(R.id.zalogujSie);
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
            startActivity(intent);
            finish();
        });

        przyciskRejestracji.setOnClickListener(new View.OnClickListener() {
            final TextView hasloJeden = findViewById(R.id.hasloPierwsze);
            final TextView hasloDwa = findViewById(R.id.hasloDrugie);


            @Override
            public void onClick(View view) {
                email = String.valueOf(edytujPoleEmail.getText());
                passwordFirst = String.valueOf(hasloJeden.getText());
                passwordSecond = String.valueOf(hasloDwa.getText());

                if (emptyFieldsErrorHandle()){
                    return;
                }

                AuthenticationManager authManager = new AuthenticationManager();

                authManager.userRegister(email, passwordFirst, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "Konto założone",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(Register.this, "Authentication failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();            }
                });
            }
        });
    }

    public void createAccountWithEmailAndPassword(String email, String passwordFirst) {

    }

    public boolean emptyFieldsErrorHandle(){
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Wprowadź e-mail", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(passwordFirst) || TextUtils.isEmpty(passwordSecond)) {
            Toast.makeText(Register.this, "Wprowadź hasło", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!passwordSecond.equals(passwordFirst)) {
            Toast.makeText(Register.this, "Hasła nie pasują do siebie.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }*/
}