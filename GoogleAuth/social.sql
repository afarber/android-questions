pragma foreign_keys = on;

create table social (
        sid text primary key,
        net integer,
        given text not null,
        family text,
        photo text,
        lat real,
        lng real,
        stamp timestamp default (strftime('%s', 'now'))
);

/*
        public static final int UNKNOWN       = 0;
        public static final int GOOGLE        = 1;
        public static final int APPLE         = 2;
        public static final int ODNOKLASSNIKI = 4;
        public static final int MAILRU        = 8;
        public static final int VKONTAKTE     = 16;
        public static final int FACEBOOK      = 32;
*/
