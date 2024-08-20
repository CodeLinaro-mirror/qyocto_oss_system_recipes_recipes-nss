DESCRIPTION = "Adding ppecfg support for RDK revision 12.5"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

FILESPATH = "${TOPDIR}/../opensource/:"

DEPENDS = "qca-nss-libppenl qca-nss-ppe jansson"
RDEPENDS_${PN} += "qca-nss-userspace-oss"

SRC_URI = "file://qca-nss-userspace-oss/netfn/"

FILESPATH = "${TOPDIR}/../opensource/:"

TARGET_LDFLAGS +="-lnl-3 -lnl-genl-3"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/libnl3 \
	-I${STAGING_INCDIR}/jansson \
	-I${STAGING_INCDIR}/qca-nss-ppe \
	-Wno-int-conversion -Wno-error=format"

S = "${WORKDIR}/qca-nss-userspace-oss/netfn/"

do_configure() {
	true
}

do_compile() {
    unset LDFLAGS
    CC="${CC}" \
    CFLAGS="${CFLAGS} -I${TARGET_CFLAGS}" \
    make -C ${S}
}

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/obj/netfn ${D}${bindir}
}

PACKAGES = "${PN}"
FILES:${PN}+= "${bindir}/netfn"
