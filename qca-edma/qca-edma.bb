SUMMARY = "Essedma driver"
DESCRIPTION = "EDMA"
SECTION = "base"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/files/copyright;md5=0a674a878fe6f6c9e1261ae3767efebd"

inherit module
inherit systemd

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-edma \
	   file://files \
	   "
DEPENDS = "virtual/kernel"
S = "${WORKDIR}/qca-edma"

PACKAGES += "kernel-module-essedma"
INSANE_SKIP:${PN} = "dev"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' \
		OS='linux' \
		"

do_configure() {
	true
}

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="arm" \
		SUBDIRS="${S}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 essedma${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/qca-edma ${D}${bindir}/qca-edma
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-edma.service ${D}${systemd_unitdir}/system/qca-edma.service
}

SYSTEMD_SERVICE:${PN} += "qca-edma.service"

FILES:${PN} = " \
	${systemd_unitdir}/system/qca-edma.service \
	${bindir}/qca-edma \
"

KERNEL_MODULE_AUTOLOAD += "essedma"
module_autoload_essedma = "qca-ssdk essedma"
