package com.shivam.neckfit;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    final int NAVY = Color.rgb(11,31,58), SAFFRON = Color.rgb(255,153,51), GREEN = Color.rgb(19,136,8), CREAM = Color.rgb(255,248,239), GREY = Color.rgb(88,96,105), GOLD = Color.rgb(255,193,7);
    LinearLayout root, content, bottomNav;
    ScrollView scrollView;
    SharedPreferences prefs;
    ArrayList<Exercise> exercises = new ArrayList<>();
    CountDownTimer timer;
    int current = 0, secondsLeft = 0;
    boolean paused = false;
    TextView timerText;
    ProgressBar timerProgress;
    ToneGenerator tone;

    static class Exercise {
        String name, hiName, subtitle, instructions, hiInstructions, benefits, hiBenefits, mistakes, coach; int seconds, sets, image;
        Exercise(String n, String hn, String sub, int sec, int set, int img, String ins, String hins, String ben, String hben, String mis, String coach){
            name=n; hiName=hn; subtitle=sub; seconds=sec; sets=set; image=img; instructions=ins; hiInstructions=hins; benefits=ben; hiBenefits=hben; mistakes=mis; this.coach=coach;
        }
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        prefs = getSharedPreferences("neckfit_pro", MODE_PRIVATE);
        tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 65);
        seedExercises();
        buildShell();
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        showSplash();
    }

    void seedExercises(){
        exercises.add(new Exercise("Chin Tuck", "चिन टक", "Forward head correction", 30, 2, R.drawable.ex_chin_tuck,
                "Sit tall. Keep eyes forward. Gently slide your chin straight back like making a small double chin. Hold softly, then release.",
                "सीधे बैठो, आंखें सामने रखो। ठोड़ी को धीरे से पीछे खींचो जैसे हल्का double chin बन रहा हो।",
                "Aligns head over shoulders and reduces tech-neck look.", "सिर और shoulders की alignment बेहतर करता है।", "Do not look down or push with your hand.", "Imagine your head sliding back on a rail."));
        exercises.add(new Exercise("Shoulder Blade Squeeze", "शोल्डर ब्लेड स्क्वीज", "Upper back activation", 30, 2, R.drawable.ex_shoulder_squeeze,
                "Pull both shoulder blades gently back and slightly down. Keep chest open and breathe normally.",
                "दोनों shoulders को हल्का पीछे और नीचे खींचो। Chest open रखो।", "Reduces rounded shoulders so the neck looks longer.", "Rounded shoulders कम करके neck को visually longer दिखाता है।", "Do not shrug shoulders up.", "Keep ears away from shoulders."));
        exercises.add(new Exercise("Neck Side Stretch", "नेक साइड स्ट्रेच", "Side neck release", 20, 2, R.drawable.ex_side_stretch,
                "Tilt your head gently to one side while keeping the opposite shoulder relaxed. Repeat on both sides.",
                "सिर को धीरे से एक side झुकाओ और opposite shoulder relaxed रखो।", "Reduces side neck tightness.", "Side neck tightness कम करता है।", "No bouncing. Stop if tingling happens.", "Stretch should feel mild, never sharp."));
        exercises.add(new Exercise("Neck Flexion Stretch", "नेक फ्लेक्शन", "Back neck relaxation", 20, 2, R.drawable.ex_flexion,
                "Slowly bring chin toward chest. Keep shoulders relaxed and breathe slowly.",
                "ठोड़ी को धीरे से chest की तरफ लाओ। Shoulders relaxed रखो।", "Relaxes back of neck after phone or laptop use.", "Phone/laptop use के बाद neck relax करता है।", "Do not pull the head hard.", "Slow breathing makes it safer."));
        exercises.add(new Exercise("Wall Posture Hold", "वॉल पोस्टर होल्ड", "Full-body alignment", 45, 2, R.drawable.ex_wall_hold,
                "Stand against a wall. Keep back of head, upper back and hips close to wall. Tuck chin slightly.",
                "Wall के साथ खड़े हो। Head, upper back और hips को wall के पास रखो।", "Trains a taller, straighter posture.", "Body को straight और tall posture train करता है।", "Do not over-arch lower back.", "This is your posture reset button."));
        exercises.add(new Exercise("Chest Opener Stretch", "चेस्ट ओपनर", "Rounded shoulder fix", 30, 2, R.drawable.ex_chest_opener,
                "Clasp hands behind your back or hold a towel. Gently open chest and keep neck long.",
                "हाथों को पीछे clasp करो या towel पकड़ो। Chest को धीरे से open करो।", "Opens chest and improves shoulder line.", "Chest open करके shoulder line improve करता है।", "Do not overextend lower back.", "Lift chest, not chin."));
        exercises.add(new Exercise("Phone Posture Practice", "फोन पोस्टर प्रैक्टिस", "Daily habit correction", 60, 1, R.drawable.ex_phone_posture,
                "Hold phone near eye level. Keep chin slightly tucked and shoulders relaxed.",
                "Phone को eye level पर रखो। Chin slight tucked और shoulders relaxed रखो।", "Prevents daily tech neck habit.", "Daily tech-neck habit को prevent करता है।", "Do not keep phone low for long scrolling.", "Bring screen to eyes, not neck to screen."));
    }

    void buildShell(){
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(bgColor());
        scrollView = new ScrollView(this); scrollView.setFillViewport(true);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(18), dp(18), dp(18), dp(18));
        scrollView.addView(content); root.addView(scrollView, new LinearLayout.LayoutParams(-1,0,1));
        bottomNav = new LinearLayout(this); bottomNav.setOrientation(LinearLayout.HORIZONTAL); bottomNav.setGravity(Gravity.CENTER); bottomNav.setPadding(dp(4), dp(4), dp(4), dp(4));
        bottomNav.setBackgroundColor(NAVY); root.addView(bottomNav, new LinearLayout.LayoutParams(-1, dp(78)));
        addNav("🏠\nHome", v->showHome()); addNav("🧘\nExercise", v->showExercises()); addNav("▶\nWorkout", v->showWorkout()); addNav("🏆\nBadges", v->showBadges()); addNav("👤\nProfile", v->showProfile());
        setContentView(root);
    }

    boolean dark(){ return prefs.getBoolean("dark", false); }
    boolean hindi(){ return prefs.getBoolean("hindi", false); }
    String lang(String en, String hi){ return hindi()?hi:en; }
    int bgColor(){ return dark()? Color.rgb(7,12,22): CREAM; }
    int cardColor(){ return dark()? Color.rgb(18,28,45): Color.WHITE; }
    int mainText(){ return dark()? Color.WHITE: NAVY; }
    int subText(){ return dark()? Color.rgb(210,218,230): GREY; }
    int strokeColor(){ return dark()? Color.rgb(50,65,88): Color.rgb(238,226,211); }
    int dp(int v){ return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    void addNav(String label, View.OnClickListener l){ TextView t = new TextView(this); t.setText(label); t.setTextColor(Color.WHITE); t.setTextSize(11); t.setGravity(Gravity.CENTER); t.setTypeface(null, Typeface.BOLD); t.setOnClickListener(l); bottomNav.addView(t, new LinearLayout.LayoutParams(0, -1, 1)); }
    TextView text(String s, int sp, int color, int style){ TextView t = new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(color); t.setTypeface(null, style); t.setLineSpacing(3,1.07f); t.setPadding(0, dp(4), 0, dp(4)); return t; }
    GradientDrawable bg(int color, float radius){ GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp((int)radius)); return g; }
    GradientDrawable strokeBg(int color, int strokeColor){ GradientDrawable g = bg(color, 20); g.setStroke(dp(1), strokeColor); return g; }
    Button button(String s, int color){ Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setTypeface(null,Typeface.BOLD); b.setAllCaps(false); b.setBackground(bg(color, 16)); b.setPadding(dp(8),0,dp(8),0); return b; }
    LinearLayout card(){ LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(18), dp(16), dp(18), dp(16)); c.setBackground(strokeBg(cardColor(), strokeColor())); LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0, dp(8),0,dp(14)); c.setLayoutParams(lp); return c; }
    void clear(){ if(timer != null) timer.cancel(); content.removeAllViews(); root.setBackgroundColor(bgColor()); scrollView.smoothScrollTo(0,0); }
    void animate(View v){ Animation a = AnimationUtils.loadAnimation(this, android.R.anim.fade_in); a.setDuration(300); v.startAnimation(a); }

    void showSplash(){
        bottomNav.setVisibility(View.GONE); clear();
        Space top = new Space(this); content.addView(top, new LinearLayout.LayoutParams(-1, dp(80)));
        TextView logo = text("🇮🇳", 58, SAFFRON, Typeface.BOLD); logo.setGravity(Gravity.CENTER); content.addView(logo);
        TextView title = text("NeckFit Pro", 36, mainText(), Typeface.BOLD); title.setGravity(Gravity.CENTER); content.addView(title);
        TextView sub = text("Premium posture coach", 18, GREEN, Typeface.BOLD); sub.setGravity(Gravity.CENTER); content.addView(sub);
        ProgressBar bar = new ProgressBar(this); content.addView(bar, new LinearLayout.LayoutParams(-1, dp(60)));
        new Handler().postDelayed(() -> { bottomNav.setVisibility(View.VISIBLE); if(!prefs.getBoolean("onboarded", false)) showOnboarding(0); else showHome(); }, 1100);
    }

    void showOnboarding(int page){
        bottomNav.setVisibility(View.GONE); clear();
        String[] titles = {"Welcome to NeckFit Pro", "Animated visual guidance", "Track streaks & reminders"};
        String[] subs = {"Neck bone length nahi badhti, lekin posture improve karke neck visually longer dikh sakti hai.", "Har exercise me image, how-to, benefits aur mistakes included hain.", "Daily progress, badges, dark mode, Hindi/English aur reminder support."};
        String[] emoji = {"🧘", "🖼️", "🏆"};
        Space top = new Space(this); content.addView(top, new LinearLayout.LayoutParams(-1, dp(55)));
        TextView e = text(emoji[page], 64, SAFFRON, Typeface.BOLD); e.setGravity(Gravity.CENTER); content.addView(e);
        TextView t = text(titles[page], 30, mainText(), Typeface.BOLD); t.setGravity(Gravity.CENTER); content.addView(t);
        TextView s = text(subs[page], 17, subText(), Typeface.NORMAL); s.setGravity(Gravity.CENTER); content.addView(s);
        LinearLayout dots = new LinearLayout(this); dots.setGravity(Gravity.CENTER); for(int i=0;i<3;i++){ TextView d=text(i==page?"●":"○",28,i==page?SAFFRON:subText(),Typeface.BOLD); dots.addView(d); } content.addView(dots);
        Button next = button(page==2 ? "Start NeckFit Pro" : "Next", page==2 ? GREEN : SAFFRON);
        next.setOnClickListener(v->{ if(page<2) showOnboarding(page+1); else { prefs.edit().putBoolean("onboarded", true).apply(); bottomNav.setVisibility(View.VISIBLE); showHome(); }});
        content.addView(next, new LinearLayout.LayoutParams(-1, dp(56)));
    }

    void hero(String title, String subtitle){
        LinearLayout h = new LinearLayout(this); h.setOrientation(LinearLayout.VERTICAL); h.setPadding(dp(20), dp(18), dp(20), dp(18));
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{SAFFRON, Color.rgb(255,237,214), GREEN}); gd.setCornerRadius(dp(24)); h.setBackground(gd);
        h.addView(text(title, 28, NAVY, Typeface.BOLD)); h.addView(text(subtitle, 15, NAVY, Typeface.BOLD));
        content.addView(h, new LinearLayout.LayoutParams(-1, -2)); animate(h);
    }

    void showHome(){
        bottomNav.setVisibility(View.VISIBLE); clear(); hero("🇮🇳 NeckFit Pro", lang("Beautiful daily posture routine", "Beautiful daily posture routine • Hindi guide"));
        LinearLayout stats = card(); stats.addView(text(lang("Today’s premium plan", "आज का premium plan"), 21, mainText(), Typeface.BOLD));
        stats.addView(text("7 exercises • 8–10 minutes • Images + timer + safe coaching", 15, subText(), Typeface.NORMAL));
        ProgressBar p = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); p.setMax(7); p.setProgress(Math.min(7, prefs.getInt("today_count",0))); stats.addView(p, new LinearLayout.LayoutParams(-1, dp(18)));
        Button start = button("▶ Start Premium Workout", SAFFRON); start.setOnClickListener(v->showWorkout()); stats.addView(start, new LinearLayout.LayoutParams(-1, dp(56))); content.addView(stats);
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.addView(statBox("🔥 Streak", prefs.getInt("streak",0)+" days")); row.addView(statBox("✅ Done", prefs.getInt("days",0)+" days")); content.addView(row);
        LinearLayout premium = card(); premium.addView(text("✨ Premium Features", 19, mainText(), Typeface.BOLD)); premium.addView(text("• Onboarding screens\n• Dark mode\n• Hindi/English toggle\n• Sound + vibration countdown\n• Streak badges\n• Calendar-style progress\n• Reminder time picker", 15, subText(), Typeface.NORMAL)); content.addView(premium);
        LinearLayout safety = card(); safety.setBackground(strokeBg(dark()?Color.rgb(20,45,32):Color.rgb(245,255,242), GREEN)); safety.addView(text("Safety reminder", 18, GREEN, Typeface.BOLD)); safety.addView(text("Bone length increase ka claim nahi. Ye app posture improvement ke liye hai. Pain, dizziness, numbness ya tingling ho to stop karo.", 14, subText(), Typeface.NORMAL)); content.addView(safety);
    }

    LinearLayout statBox(String a, String b){ LinearLayout s = new LinearLayout(this); s.setOrientation(LinearLayout.VERTICAL); s.setPadding(dp(12),dp(14),dp(12),dp(14)); s.setBackground(strokeBg(cardColor(), strokeColor())); LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(dp(4),dp(8),dp(4),dp(8)); s.setLayoutParams(lp); s.addView(text(a,14,mainText(),Typeface.BOLD)); s.addView(text(b,21,SAFFRON,Typeface.BOLD)); return s; }

    void showExercises(){
        clear(); hero("Exercise Library", "Image, how-to, benefit, mistakes");
        for(Exercise e: exercises){
            LinearLayout c = card();
            ImageView img = new ImageView(this); img.setImageResource(e.image); img.setAdjustViewBounds(true); img.setScaleType(ImageView.ScaleType.CENTER_CROP); c.addView(img, new LinearLayout.LayoutParams(-1, dp(165)));
            c.addView(text(hindi()?e.hiName:e.name, 22, mainText(), Typeface.BOLD)); c.addView(text(e.subtitle, 14, SAFFRON, Typeface.BOLD));
            c.addView(text("⏱ " + e.seconds + " sec  •  Sets: " + e.sets, 14, GREEN, Typeface.BOLD));
            c.addView(text("How to do: " + (hindi()?e.hiInstructions:e.instructions), 14, subText(), Typeface.NORMAL));
            c.addView(text("Benefit: " + (hindi()?e.hiBenefits:e.benefits), 14, mainText(), Typeface.NORMAL));
            c.addView(text("Coach tip: " + e.coach, 13, GREEN, Typeface.BOLD));
            c.addView(text("Avoid: " + e.mistakes, 13, Color.rgb(205,70,45), Typeface.BOLD));
            content.addView(c); animate(c);
        }
    }

    void showWorkout(){ current=0; showWorkoutExercise(); }
    void showWorkoutExercise(){
        clear(); if(current >= exercises.size()){ finishWorkout(); return; }
        Exercise e = exercises.get(current); hero("Workout Mode", (current+1) + "/" + exercises.size() + " • " + (hindi()?e.hiName:e.name));
        LinearLayout c = card();
        ImageView img = new ImageView(this); img.setImageResource(e.image); img.setAdjustViewBounds(true); img.setScaleType(ImageView.ScaleType.CENTER_CROP); c.addView(img, new LinearLayout.LayoutParams(-1, dp(178)));
        c.addView(text(hindi()?e.hiName:e.name, 24, mainText(), Typeface.BOLD)); c.addView(text(hindi()?e.hiInstructions:e.instructions, 15, subText(), Typeface.NORMAL));
        c.addView(text("Sets: " + e.sets + " • Coach: " + e.coach, 14, GREEN, Typeface.BOLD));
        timerText = text(String.valueOf(e.seconds), 58, GREEN, Typeface.BOLD); timerText.setGravity(Gravity.CENTER); c.addView(timerText);
        timerProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); timerProgress.setMax(e.seconds); timerProgress.setProgress(e.seconds); c.addView(timerProgress, new LinearLayout.LayoutParams(-1, dp(18)));
        LinearLayout buttons = new LinearLayout(this); buttons.setOrientation(LinearLayout.HORIZONTAL);
        Button start = button("Start", SAFFRON); Button pause = button("Pause", NAVY); Button next = button("Next", GREEN);
        start.setOnClickListener(v->{ secondsLeft = e.seconds; startTimer(e); });
        pause.setOnClickListener(v->{ if(timer!=null){ timer.cancel(); paused=true; Toast.makeText(this,"Paused",Toast.LENGTH_SHORT).show(); }});
        next.setOnClickListener(v->{ if(timer!=null) timer.cancel(); current++; showWorkoutExercise(); });
        buttons.addView(start, new LinearLayout.LayoutParams(0, dp(54), 1)); buttons.addView(pause, new LinearLayout.LayoutParams(0, dp(54), 1)); buttons.addView(next, new LinearLayout.LayoutParams(0, dp(54), 1)); c.addView(buttons);
        c.addView(text("🔊 Last 3 seconds me beep + vibration milega.", 14, SAFFRON, Typeface.BOLD));
        content.addView(c); animate(c);
    }
    void startTimer(Exercise e){
        if(timer!=null) timer.cancel(); int startFrom = (paused && secondsLeft>0) ? secondsLeft : e.seconds; paused=false;
        timer = new CountDownTimer(startFrom*1000L, 1000){
            public void onTick(long ms){ secondsLeft=(int)(ms/1000)+1; timerText.setText(String.valueOf(secondsLeft)); timerProgress.setProgress(secondsLeft); if(secondsLeft<=3){ beep(); vibrate(80); } }
            public void onFinish(){ timerText.setText("Done"); timerProgress.setProgress(0); beep(); vibrate(300); Toast.makeText(MainActivity.this,"Exercise complete",Toast.LENGTH_SHORT).show(); current++; new Handler().postDelayed(() -> showWorkoutExercise(), 650); }
        }; timer.start();
    }
    void beep(){ try{ tone.startTone(ToneGenerator.TONE_PROP_BEEP, 120); }catch(Exception ignored){} }
    void vibrate(int ms){ try{ ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(ms); }catch(Exception ignored){} }
    void finishWorkout(){
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences.Editor ed = prefs.edit();
        if(!today.equals(prefs.getString("last_day", ""))){ ed.putString("last_day", today); ed.putInt("days", prefs.getInt("days",0)+1); ed.putInt("streak", prefs.getInt("streak",0)+1); ed.putInt("total", prefs.getInt("total",0)+1); ed.putString("history_"+today,"1"); }
        ed.putInt("today_count", 7).apply();
        clear(); hero("🎉 Workout complete", "Great posture daily = cleaner neck look");
        LinearLayout c = card(); c.addView(text("Great job bhai!", 24, GREEN, Typeface.BOLD)); c.addView(text("Aaj ka routine complete. Kal fir repeat karna. Real bone length nahi badhti, visual posture improve hota hai.", 15, subText(), Typeface.NORMAL)); Button badges=button("View Badges", SAFFRON); badges.setOnClickListener(v->showBadges()); c.addView(badges, new LinearLayout.LayoutParams(-1, dp(56))); content.addView(c);
    }

    void showBadges(){
        clear(); hero("🏆 Progress & Badges", "Consistency ko game jaisa banao");
        int days = prefs.getInt("days",0), streak=prefs.getInt("streak",0), total=prefs.getInt("total",0);
        content.addView(statBox("Total workouts", String.valueOf(total))); content.addView(statBox("Current streak", streak+" days")); content.addView(statBox("Completed days", days+" days"));
        LinearLayout badges = card(); badges.addView(text("Achievements", 20, mainText(), Typeface.BOLD));
        badges.addView(text((days>=1?"✅":"🔒")+" First Workout", 16, days>=1?GREEN:subText(), Typeface.BOLD));
        badges.addView(text((streak>=3?"✅":"🔒")+" 3-Day Streak", 16, streak>=3?GREEN:subText(), Typeface.BOLD));
        badges.addView(text((streak>=7?"✅":"🔒")+" 7-Day Discipline", 16, streak>=7?GOLD:subText(), Typeface.BOLD));
        badges.addView(text((total>=21?"✅":"🔒")+" Posture Warrior", 16, total>=21?GOLD:subText(), Typeface.BOLD));
        content.addView(badges);
        LinearLayout cal = card(); cal.addView(text("Monthly calendar", 20, mainText(), Typeface.BOLD));
        GridLayout grid = new GridLayout(this); grid.setColumnCount(7); String today = new SimpleDateFormat("yyyy-MM-", Locale.US).format(new Date());
        for(int i=1;i<=31;i++){ TextView d=text(String.valueOf(i),13,mainText(),Typeface.BOLD); d.setGravity(Gravity.CENTER); d.setBackground(bg(prefs.getString("history_"+today+String.format(Locale.US,"%02d",i),"").equals("1")?GREEN:(dark()?Color.rgb(35,48,68):Color.rgb(245,241,235)), 12)); GridLayout.LayoutParams glp = new GridLayout.LayoutParams(); glp.width=dp(42); glp.height=dp(42); glp.setMargins(dp(3),dp(3),dp(3),dp(3)); grid.addView(d, glp); }
        cal.addView(grid); content.addView(cal);
    }

    void showProfile(){
        clear(); hero("Profile & Settings", "Premium customization");
        LinearLayout c=card(); EditText name=new EditText(this); name.setHint("Your name"); name.setText(prefs.getString("name","Shivam")); name.setTextColor(mainText()); name.setHintTextColor(subText()); c.addView(name);
        Button save=button("Save profile", SAFFRON); save.setOnClickListener(v->{ prefs.edit().putString("name",name.getText().toString()).apply(); Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show(); }); c.addView(save, new LinearLayout.LayoutParams(-1, dp(54)));
        Button langBtn=button(hindi()?"Switch to English":"Hindi Mode चालू करो", GREEN); langBtn.setOnClickListener(v->{ prefs.edit().putBoolean("hindi", !hindi()).apply(); showProfile(); }); c.addView(langBtn, new LinearLayout.LayoutParams(-1, dp(54)));
        Button darkBtn=button(dark()?"Light Mode":"Dark Mode", NAVY); darkBtn.setOnClickListener(v->{ prefs.edit().putBoolean("dark", !dark()).apply(); showProfile(); }); c.addView(darkBtn, new LinearLayout.LayoutParams(-1, dp(54)));
        Button rem=button("Set daily reminder time", GREEN); rem.setOnClickListener(v->showTimePicker()); c.addView(rem, new LinearLayout.LayoutParams(-1, dp(54)));
        c.addView(text("Current reminder: " + prefs.getString("rem_time","07:00"), 14, subText(), Typeface.BOLD));
        Button reset=button("Reset progress", Color.rgb(150,50,60)); reset.setOnClickListener(v->{ prefs.edit().clear().apply(); Toast.makeText(this,"Progress reset",Toast.LENGTH_SHORT).show(); showOnboarding(0); }); c.addView(reset, new LinearLayout.LayoutParams(-1, dp(54))); content.addView(c);
        LinearLayout safety=card(); safety.addView(text("Medical safety",20,mainText(),Typeface.BOLD)); safety.addView(text("This app is for posture and fitness guidance only. Do not force neck movement. Stop if pain, dizziness, numbness or tingling occurs. Consult a doctor/physiotherapist if pain continues.",15,subText(),Typeface.NORMAL)); content.addView(safety);
    }

    void showTimePicker(){
        Calendar now = Calendar.getInstance();
        TimePickerDialog dlg = new TimePickerDialog(this, (view, hour, minute) -> setReminder(hour, minute), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        dlg.show();
    }
    void setReminder(int hour, int minute){
        Intent i = new Intent(this, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 99, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar cal = Calendar.getInstance(); cal.set(Calendar.HOUR_OF_DAY, hour); cal.set(Calendar.MINUTE, minute); cal.set(Calendar.SECOND, 0); if(cal.before(Calendar.getInstance())) cal.add(Calendar.DATE, 1);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        String time = String.format(Locale.US, "%02d:%02d", hour, minute); prefs.edit().putBoolean("reminder", true).putString("rem_time", time).apply(); Toast.makeText(this,"Daily reminder set for "+time,Toast.LENGTH_LONG).show();
    }
}
