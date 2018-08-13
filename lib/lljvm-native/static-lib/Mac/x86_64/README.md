## The static libraries w/PIC in this dir built by g++ v4.9.4 on macOS Sierra v10.12.1

### libz.a

  $ wget http://sourceforge.mirrorservice.org/l/li/libpng/zlib/1.2.8/zlib-1.2.8.tar.gz
  $ tar xvf zlib-1.2.8.tar.gz
  $ cd zlib-1.2.8
  $ gcc-4.9 -O3 -fPIC -D_LARGEFILE64_SOURCE=1 -DHAVE_HIDDEN -DPIC -c adler32.c crc32.c deflate.c infback.c inffast.c inflate.c inftrees.c trees.c zutil.c compress.c uncompr.c gzclose.c gzwrite.c gzread.c gzlib.c
  $ ar rc libz-1.2.8.a adler32.o crc32.o deflate.o infback.o inffast.o inflate.o inftrees.o trees.o zutil.o compress.o uncompr.o gzclose.o gzlib.o gzread.o gzwrite.o

### libtinfo.a

  $ wget https://ftp.gnu.org/pub/gnu/ncurses/ncurses-5.7.tar.gz
  $ tar xvf ncurses-5.7.tar.gz
  $ cd ncurses-5.7
  // Drops `--no-cpp-precomp` in configure because this is an obsolete Apple GCC option
  $ CC=gcc-4.9 ./configure --with-shared --without-debug
  $ make -j4
  // `lib_s` includes compiled objects w/PIC
  $ cd lib_s
  $ ar rv libncurses-5.7.a hardscroll.o hashmap.o lib_addch.o lib_addstr.o lib_beep.o lib_bkgd.o lib_box.o lib_chgat.o lib_clear.o lib_clearok.o lib_clrbot.o lib_clreol.o lib_color.o lib_colorset.o lib_delch.o lib_delwin.o lib_echo.o lib_endwin.o lib_erase.o lib_flash.o lib_gen.o lib_getch.o lib_getstr.o lib_hline.o lib_immedok.o lib_inchstr.o lib_initscr.o lib_insch.o lib_insdel.o lib_insnstr.o lib_instr.o lib_isendwin.o lib_leaveok.o lib_mouse.o lib_move.o lib_mvcur.o lib_mvwin.o lib_newterm.o lib_newwin.o lib_nl.o lib_overlay.o lib_pad.o lib_printw.o lib_redrawln.o lib_refresh.o lib_restart.o lib_scanw.o lib_screen.o lib_scroll.o lib_scrollok.o lib_scrreg.o lib_set_term.o lib_slk.o lib_slkatr_set.o lib_slkatrof.o lib_slkatron.o lib_slkatrset.o lib_slkattr.o lib_slkclear.o lib_slkcolor.o lib_slkinit.o lib_slklab.o lib_slkrefr.o lib_slkset.o lib_slktouch.o lib_touch.o lib_tstp.o lib_ungetch.o lib_vidattr.o lib_vline.o lib_wattroff.o lib_wattron.o lib_winch.o lib_window.o nc_panel.o safe_sprintf.o tty_update.o varargs.o memmove.o vsscanf.o lib_freeall.o expanded.o legacy_coding.o lib_dft_fgbg.o lib_print.o resizeterm.o use_screen.o use_window.o wresize.o access.o add_tries.o alloc_ttype.o codes.o comp_error.o db_iterator.o doalloc.o entries.o fallback.o free_ttype.o getenv_num.o home_terminfo.o init_keytry.o lib_acs.o lib_baudrate.o lib_cur_term.o lib_data.o lib_has_cap.o lib_kernel.o lib_keyname.o lib_longname.o lib_napms.o lib_options.o lib_raw.o lib_setup.o lib_termcap.o lib_termname.o lib_tgoto.o lib_ti.o lib_tparm.o lib_tputs.o lib_trace.o lib_ttyflags.o lib_twait.o name_match.o names.o read_entry.o read_termcap.o setbuf.o strings.o tries.o trim_sgr0.o unctrl.o visbuf.o alloc_entry.o captoinfo.o comp_captab.o comp_expand.o comp_hash.o comp_parse.o comp_scan.o parse_entry.o write_entry.o define_key.o hashed_db.o key_defined.o keybound.o keyok.o version.o
