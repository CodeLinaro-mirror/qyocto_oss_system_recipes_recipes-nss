DESCRIPTION = "Adding ppecfg support for RDK revision 12.5"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-userspace-oss/ppe/ppecfg \
		"

TARGET_LDFLAGS ="-lnl-3 -lnl-ppe -lnl-genl-3 -pie -ljson-c"
TARGET_CFLAGS = "-I${STAGING_INCDIR}/libnl3 -I${STAGING_INCDIR}/qca-nss-ppe -I${STAGING_INCDIR}/qca-nss-dp -I${S}/include -Wno-int-conversion"

TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES','lgi_qca',' -O ','',d)}"

DEPENDS = "libnl qca-nss-ppe qca-nss-dp qca-nss-libppenl json-c"

RDEPENDS:${PN} += "qca-nss-libppenl"

S = "${WORKDIR}/qca-nss-userspace-oss/ppe/ppecfg"

do_compile() {
        unset LDFLAGS
        CC="${CC}" \
        LIBS="-L${STAGING_LIBDIR} ${TARGET_LDFLAGS}" \
	CFLAGS="${CFLAGS} ${TARGET_CFLAGS}" \
	SoC='${SOC_TYPE}' \
	make -C ${S}
}

do_install() {
        install -d ${D}/${bindir}/
        install -m 0744 ${S}/obj/ppecfg ${D}/${bindir}/
}

FILES:${PN} += "${bindir}/ppecfg \
               "

