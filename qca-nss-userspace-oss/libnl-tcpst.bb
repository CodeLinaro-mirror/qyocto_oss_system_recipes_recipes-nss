DESCRIPTION = "Library to facilitate communication from user to kernel for the TCP ST netlink families"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://nss-speedtest/nss-tcp-st"

DEPENDS = "libnl qca-nss-netfn-tcpst"

S = "${WORKDIR}/nss-speedtest/nss-tcp-st"

TARGET_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-netfn-tcpst/ \
		-I${STAGING_INCDIR}/libnl3 \
		-I${S}/include"

TARGET_LDFLAGS = "-L${STAGING_INCDIR}/nss-tcp-st"

do_compile() {
	unset LDFLAGS
	CC="${CC}" \
	CFLAGS="${TARGET_CFLAGS}" \
	LIBS="${TARGET_LDFLAGS}" \
	make -C ${S}
}


do_install() {
	install -d ${D}/${libdir}
	install -d ${D}${includedir}
	install -m 0644 ${S}/include/* ${D}${includedir}
	install -m 0744 ${S}/obj/libnl-tcpst.so ${D}/${libdir}
}

INSANE_SKIP:${PN} += "ldflags"
INSANE_SKIP:${PN} += "debug-files"

PACKAGES = "${PN}"

FILES:${PN} += "${includedir}/nss-tcp-st.h \
		${includedir}/nss-tcp-st-log.h \
		${libdir}/libnl-tcpst.so \
		${libdir}/.debug \
		${libdir}/.debug/libnl-tcpst.so \
		"

