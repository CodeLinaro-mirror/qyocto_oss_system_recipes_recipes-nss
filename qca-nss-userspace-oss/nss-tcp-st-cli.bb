DESCRIPTION = "Userspace utility for nss tcp speedtest"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://nss-speedtest/nss-tcp-st-cli"

DEPENDS = "libnl qca-nss-netfn-tcpst libnl-tcpst curl"

RDEPENDS:${PN} += "libnl-tcpst"

S = "${WORKDIR}/nss-speedtest/nss-tcp-st-cli"

TARGET_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-netfn-tcpst/ \
		-I${STAGING_INCDIR}/libnl3 \
		-I${STAGING_INCDIR}/libnl-tcpst \
		"
TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES','ofw_qca',' -Wno-stringop-overflow -Wno-unused-result ','',d)}"

TARGET_LDFLAGS = "-L${STAGING_INCDIR}/nss-tcp-st"

do_compile() {
	unset LDFLAGS
	CC="${CC}" \
	CFLAGS="${TARGET_CFLAGS}" \
	LIBS="${TARGET_LDFLAGS}" \
	make -C ${S}
}


do_install() {
	install -d ${D}/usr/sbin
	install -m 0755 ${S}/obj/nss-tcp-st ${D}/usr/sbin
}
