DESCRIPTION = "Adding ppecfg support for RDK revision 12.5"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/qca-nss-userspace-oss/ppe/ppenl_lib/nss_ppenl_acl.h;beginline=1;endline=15;md5=4d17d79676f3c8856dd85d6790e73c47"

FILESPATH = "${TOPDIR}/../opensource/:"

DEPENDS = "libnl qca-nss-ppe"

RDEPENDS_${PN} += "qca-nss-ppe-netlink"

SRC_URI = "file://qca-nss-userspace-oss/ppe/ppenl_lib"

FILESPATH = "${TOPDIR}/../opensource/:"

TARGET_LDFLAGS +="-lpthread -lnl-3 -lnl-genl-3 -pie"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/libnl3 -I${STAGING_INCDIR}/qca-nss-ppe -I${S}/include -Wno-int-conversion"


S = "${WORKDIR}/qca-nss-userspace-oss/ppe/ppenl_lib"

do_compile() {
        unset LDFLAGS
        CC="${CC}" \
        CFLAGS="${CFLAGS} -I${TARGET_CFLAGS}" \
        LIBS="-L${STAGING_LIBDIR}/ppenl_lib ${TARGET_LDFLAGS}" \
        make -C ${S}
}

do_install() {
        install -d ${D}${libdir}
        install -d ${D}${includedir}
        install -m 0744 ${S}/obj/libnl-ppe.so ${D}/${libdir}
}

do_install_append() {
    install -d ${D}${includedir}/libnl-ppe
    install -m 0644 ${S}/include/* ${D}${includedir}/libnl-ppe
}

FILES:${PN} += "${libdir}/libnl-ppe.so"
FILES_${PN}-dev = "${includedir}/libnl-ppe"

