SUMMARY = "Essedma init script"
DESCRIPTION = "This package adds the script, which load-balances edma interrupts across multiple cores "
SECTION = "base"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/copyright;md5=0a674a878fe6f6c9e1261ae3767efebd"
PR = "r7"

inherit update-alternatives
inherit update-rc.d

SRC_URI = "file://qca-edma \
	   file://copyright"

S = "${WORKDIR}"

do_compile()  {
	:
}

do_install () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/qca-edma ${D}${sysconfdir}/init.d/qca-edma
}

PACKAGE_ARCH_qemuall = "${MACHINE_ARCH}"

CONFFILES_${PN} = "${sysconfdir}/network/interfaces"
INITSCRIPT_PACKAGES = "qca-edma"
INITSCRIPT_NAME = "qca-edma"
INITSCRIPT_PARAMS = "start 99 S . stop 99 0 6 1 ."
