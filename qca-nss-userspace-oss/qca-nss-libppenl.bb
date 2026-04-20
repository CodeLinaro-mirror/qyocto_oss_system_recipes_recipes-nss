DESCRIPTION = "Adding ppecfg support for RDK revision 12.5"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"
FILESPATH = "${TOPDIR}/../opensource/:"

DEPENDS = "libnl qca-nss-ppe"

SRC_URI = "file://qca-nss-userspace-oss/ppe/ppenl_lib"


FILESPATH = "${TOPDIR}/../opensource/:"

TARGET_LDFLAGS +="-lnl-3 -lnl-genl-3 -pie"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/libnl3 -I${STAGING_INCDIR}/qca-nss-ppe -I${S}/include -Wno-int-conversion"


S = "${WORKDIR}/qca-nss-userspace-oss/ppe/ppenl_lib"

do_compile() {
        unset LDFLAGS
        CC="${CC}" \
        CFLAGS="${CFLAGS} -I${TARGET_CFLAGS}" \
	LIBS="-L${STAGING_LIBDIR} ${TARGET_LDFLAGS}" \
        make -C ${S}
}

do_install() {
        install -d ${D}/${libdir}
	install -d ${D}${includedir}
	install -m 0644 ${S}/include/*.h ${D}${includedir}
        install -m 0744 ${S}/obj/libnl-ppe.so ${D}/${libdir}
}

INSANE_SKIP:${PN} += "ldflags"
INSANE_SKIP:${PN} += "debug-files"

PACKAGES = "${PN}"

FILES:${PN} += "${includedir}/*.h \
		${libdir}/libnl-ppe.so \
		${libdir}/.debug/libnl-ppe.so \
               "
