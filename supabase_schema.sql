CREATE TABLE IF NOT EXISTS public.users (
    firebase_uid TEXT PRIMARY KEY,
    display_name TEXT,
    email TEXT,
    premium_status BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.plants (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    species TEXT,
    min_lux INT,
    max_lux INT,
    water_interval_days INT,
    image_url TEXT,
    description TEXT,
    price DECIMAL(10, 2),
    currency TEXT DEFAULT 'INR'
);

CREATE TABLE IF NOT EXISTS public.reminders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id TEXT REFERENCES public.users(firebase_uid) ON DELETE CASCADE,
    plant_id UUID REFERENCES public.plants(id) ON DELETE CASCADE,
    last_watered_at TIMESTAMPTZ,
    next_reminder_at TIMESTAMPTZ
);
