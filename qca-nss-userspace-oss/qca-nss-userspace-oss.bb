DESCRIPTION = "Adding ppecfg support for RDK revision 12.5"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/qca-nss-userspace-oss/ppe/ppecfg/ppecfg_acl.h;beginline=1;endline=15;md5=eeb26884f344989787b2a4d72cf11140"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-userspace-oss/ppe/ppecfg \
		"

TARGET_LDFLAGS ="-lnl-3 -lnl-ppe -lnl-genl-3 -pie -ljson-c"
TARGET_CFLAGS = "-I${STAGING_INCDIR}/libnl3 -I${STAGING_INCDIR}/qca-nss-ppe -I${S}/include -Wno-int-conversion"

TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES','ofw_qca',' -O ','',d)}"

DEPENDS = "libnl qca-nss-ppe qca-nss-libppenl json-c"

RDEPENDS:${PN} += "qca-nss-libppenl"

S = "${WORKDIR}/qca-nss-userspace-oss/ppe/ppecfg"

do_compile() {
        unset LDFLAGS
        CC="${CC}" \
        LIBS="-L${STAGING_LIBDIR} ${TARGET_LDFLAGS}" \
	CFLAGS="${CFLAGS} ${TARGET_CFLAGS}" \
	make -C ${S}
}

do_install() {
        install -d ${D}/${bindir}/
        install -m 0744 ${S}/obj/ppecfg ${D}/${bindir}/
}

FILES:${PN} += "${bindir}/ppecfg \
               "

