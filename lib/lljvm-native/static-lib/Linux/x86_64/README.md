## The static libraries w/PIC in this dir built by g++ v4.8.5 on Amazon Linux AMI (ami-0ad99772)

### libz.a

  $ wget http://sourceforge.mirrorservice.org/l/li/libpng/zlib/1.2.8/zlib-1.2.8.tar.gz
  $ tar xvf zlib-1.2.8.tar.gz
  $ cd zlib-1.2.8
  $ gcc -O3 -fPIC -D_LARGEFILE64_SOURCE=1 -DHAVE_HIDDEN -DPIC -c adler32.c crc32.c deflate.c infback.c inffast.c inflate.c inftrees.c trees.c zutil.c compress.c uncompr.c gzclose.c gzwrite.c gzread.c gzlib.c
  $ ar rc libz-1.2.8.a adler32.o crc32.o deflate.o infback.o inffast.o inflate.o inftrees.o trees.o zutil.o compress.o uncompr.o gzclose.o gzlib.o gzread.o gzwrite.o

### libtinfo.a

  $ wget https://ftp.gnu.org/pub/gnu/ncurses/ncurses-5.7.tar.gz
  $ tar xvf ncurses-5.7.tar.gz
  $ cd ncurses-5.7
  $ ./configure --with-shared --with-termlib --without-debug
  $ make -j4
  // `lib_s` includes compiled objects w/PIC
  $ cd lib_s
  $ ar rv libtinfo-5.7.a access.o add_tries.o alloc_ttype.o codes.o comp_error.o db_iterator.o doalloc.o entries.o fallback.o free_ttype.o getenv_num.o home_terminfo.o init_keytry.o lib_acs.o lib_baudrate.o lib_cur_term.o lib_data.o lib_has_cap.o lib_kernel.o lib_keyname.o lib_longname.o lib_napms.o lib_options.o lib_raw.o lib_setup.o lib_termcap.o lib_termname.o lib_tgoto.o lib_ti.o lib_tparm.o lib_tputs.o lib_trace.o lib_ttyflags.o lib_twait.o name_match.o names.o read_entry.o read_termcap.o setbuf.o strings.o tries.o trim_sgr0.o unctrl.o visbuf.o alloc_entry.o captoinfo.o comp_captab.o comp_expand.o comp_hash.o comp_parse.o comp_scan.o parse_entry.o write_entry.o define_key.o hashed_db.o key_defined.o keybound.o keyok.o version.o
